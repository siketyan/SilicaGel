package me.siketyan.silicagel.misskeyapi

import org.json.JSONArray

class Permission
constructor(vararg private val permission: Name) {
    enum class Name(val permissionName: String) {
        ACCOUNT_READ("account-read"),
        ACCOUNT_WRITE("account-write"),
        NOTE_WRITE("note-write"),
        REACTION_WRITE("reaction-write"),
        FOLLOWING_WRITE("following-write"),
        DRIVE_READ("drive-read"),
        DRIVE_WRITE("drive-write"),
        NOTIFICATION_READ("notification-read"),
        NOTIFICATION_WRITE("notification-write")
    }

    //`permission: ["note-write", "account-read"]`

    fun toJSONArray(): JSONArray{
        var j = JSONArray()
        permission.iterator().forEach {
            j.apply {
                put(it.permissionName)
            }
        }
        return j
    }
}
