import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

const val PATH = "Theresa/"
const val CLIENT_PATH = "${PATH}Load.jar"

fun main() {
    println("Initializing...")
    val port = 37254 //Part
    val tcpServer = ServerSocket(port)

    println("Waiting for connection...")
    while (!tcpServer.isClosed) {
        val clientSocket: Socket = tcpServer.accept()
        val client = Client(clientSocket)
        client.start()
    }
}

class Client(private val clientSocket: Socket) : Thread() {

    override fun run() {
        val input = DataInputStream(clientSocket.getInputStream())
        val output = DataOutputStream(clientSocket.getOutputStream())
        while (true) {
            if (clientSocket.isClosed) break
            val received: String = try {
                input.readUTF()
            } catch (e: Exception) {
                return
            }
            clientSocket.inetAddress.hostAddress ?: return
            val ip = clientSocket.inetAddress.hostAddress.toString()
            if (received == "HIHI") {
                output.writeUTF("Passed")
                println("Sending file to $ip...")
                sendFile(clientSocket, output, System.nanoTime())
            }
        }
    }


    private fun sendFile(socket: Socket, output: DataOutputStream, startTime: Long) {
        val fileStream = DataInputStream(BufferedInputStream(FileInputStream(CLIENT_PATH)))
        val bufferSize = 8192
        val buf = ByteArray(bufferSize)
        try {
            while (true) {
                val read: Int = fileStream.read(buf)
                if (read == -1) {
                    break
                }
                output.write(buf, 0, read)
            }
            output.flush()
            fileStream.close()
            socket.close()
            println("Finish sending file in " + String.format("%.3f", (System.nanoTime() - startTime) / 1E9) + " seconds")
        } catch (e: SocketException) {
            println("Connection reset, client disconnected")
        }
    }
}