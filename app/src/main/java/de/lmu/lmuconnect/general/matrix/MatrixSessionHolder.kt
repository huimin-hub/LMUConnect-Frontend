package de.lmu.lmuconnect.general.matrix

import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.model.RoomSummary

object MatrixSessionHolder {
    interface MatrixSessionSetListener {
        fun matrixSessionSet()
    }

    private var currentSession: Session? = null

    private var matrixSessionSetListeners: ArrayList<MatrixSessionSetListener> = arrayListOf()

    lateinit var currentUserId: String

    lateinit var currentUserName: String

    lateinit var personalList: ArrayList<RoomSummary>

    lateinit var groupList: ArrayList<RoomSummary>

    lateinit var currentRoomID: String

    // FUNCTIONS
    fun addMatrixSessionSetListener(matrixSessionSetListener: MatrixSessionSetListener) =
        matrixSessionSetListeners.add(matrixSessionSetListener)

    fun removeMatrixSessionSetListener(matrixSessionSetListener: MatrixSessionSetListener) =
        matrixSessionSetListeners.remove(matrixSessionSetListener)

    fun setCurrentSession(session: Session) {
        currentSession = session
        for (listener in matrixSessionSetListeners) listener.matrixSessionSet()
    }

    fun getCurrentSession(): Session? = currentSession
}