package de.lmu.lmuconnect

import android.app.Application
import android.content.Context
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import de.lmu.lmuconnect.general.matrix.RoomDisplayNameFallbackProviderImpl
import de.lmu.lmuconnect.study.notifications.NotificationHandler
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.api.session.getUser

private const val TAG = "SocialApplication"

class SocialApplication : Application(), MatrixSessionHolder.MatrixSessionSetListener {

    // Init
    private lateinit var matrix: Matrix
    private lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()

        MatrixSessionHolder.addMatrixSessionSetListener(this)

        sessionManager = SessionManager(this)
        createMatrix()

        val lastSession = matrix.authenticationService().getLastAuthenticatedSession()

        if(lastSession != null) {
            MatrixSessionHolder.setCurrentSession(lastSession)
            lastSession.open()
            lastSession.syncService().startSync(true)
            MatrixSessionHolder.currentUserId = lastSession.myUserId
            MatrixSessionHolder.currentUserName = lastSession.getUser(lastSession.myUserId)?.displayName.toString()
        }

        // TODO: remove

        NotificationHandler.createNotificationChannel(applicationContext)
        NotificationHandler.fetchEventsToday(this)
    }

    override fun onTerminate() {
        MatrixSessionHolder.removeMatrixSessionSetListener(this)
        super.onTerminate()
    }

    private fun createMatrix() {
        matrix = Matrix(
            context = this,
            matrixConfiguration = MatrixConfiguration(
                roomDisplayNameFallbackProvider = RoomDisplayNameFallbackProviderImpl()
            )
        )
    }

    companion object {
        fun getMatrix(context: Context): Matrix {
            return (context.applicationContext as SocialApplication).matrix
        }
    }

    override fun matrixSessionSet() {

    }
}