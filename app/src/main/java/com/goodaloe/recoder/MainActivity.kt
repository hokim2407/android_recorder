package com.goodaloe.recoder

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private val recordBtn: RecordButton by lazy {
        findViewById(R.id.recordBtn)
    }
    private var state = State.BEFORE_RECORDING
    private  val requiredPermissions = arrayOf(Manifest.permission.RECORD_AUDIO) ;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAudioPermission();

        initView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val audioRecordPermissionGranted =
                (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) &&
                (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED)
        if(!audioRecordPermissionGranted){
            finish()
        }

    }

    private  fun requestAudioPermission(){
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun initView() {
        recordBtn.updateIconWithState(state)

    }

    companion object{
        private  const val REQUEST_RECORD_AUDIO_PERMISSION = 201;
    }
}