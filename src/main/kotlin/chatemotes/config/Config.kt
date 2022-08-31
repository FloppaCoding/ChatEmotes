package chatemotes.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.nio.charset.StandardCharsets

class Config(path: File) {

    private val file = File(path, "config.json")
    var enabled = true

    init {
        // This gets run before the pre initialization event (it gets run when the Companion object is created)
        // therefore the directory did not get created by the preInit handler.
        // It is created here
        if (!path.exists()) {
            path.mkdirs()
        }
        // create file if it doesn't exist
        file.createNewFile()

        load()
    }

    fun load() {
        try {
            enabled = Gson().fromJson(file.readText(), Boolean::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun save() {
        try {
            file.bufferedWriter(StandardCharsets.UTF_8).run {
                write(GsonBuilder().setPrettyPrinting().create().toJson(enabled))
                close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}