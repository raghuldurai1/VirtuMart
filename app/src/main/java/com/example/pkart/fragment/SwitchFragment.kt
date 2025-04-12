package com.example.pkart.fragment

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pkart.R
import java.io.File

class SwitchFragment : Fragment() {

    private val apkUrl = "https://firebasestorage.googleapis.com/v0/b/pkart-8e638.firebasestorage.app/o/apk%2FUNITY1.apk?alt=media&token=b83e8fb6-b2ac-4f36-b9d3-0eba8f9e571f"
    private val apkFileName = "unity.apk"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_switch, container, false)
        val btnDownload = view.findViewById<Button>(R.id.btnDownload)

        btnDownload.setOnClickListener {
            downloadApk()
        }

        return view
    }

    private fun downloadApk() {
        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(apkUrl)

        // Save in app-specific storage
        val destination = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), apkFileName)
        val destinationUri = Uri.fromFile(destination)

        val request = DownloadManager.Request(uri)
            .setTitle("Downloading APK")
            .setDescription("Downloading the app installer")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(destinationUri)  // Save in app-specific directory

        downloadManager.enqueue(request)
        Toast.makeText(requireContext(), "Downloading APK...", Toast.LENGTH_SHORT).show()
    }
}
