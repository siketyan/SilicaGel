package me.siketyan.silicagel.misskeyapi.api.endpoints

import me.siketyan.silicagel.misskeyapi.MisskeyClient
import me.siketyan.silicagel.misskeyapi.MisskeyRequest
import me.siketyan.silicagel.misskeyapi.api.entities.DriveFile
import okhttp3.MultipartBody

class drive(private val client: MisskeyClient) {

    fun createFiles(i: String, file: MultipartBody.Part): MisskeyRequest<DriveFile> {
        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("i", i)
                .addPart(file)
                .build()
        return MisskeyRequest<DriveFile>(
                {
                    client.post("drive/files/create", requestBody)
                },
                {
                    client.getSerializer().fromJson(it, DriveFile::class.java)
                }
        )
    }
}