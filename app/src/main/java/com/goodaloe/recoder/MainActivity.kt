package com.goodaloe.recoder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private val recordBtn: RecordButton by lazy {
        findViewById(R.id.recordBtn)
    }

    private val resetBtn: Button by lazy {
        findViewById(R.id.resetBtn)
    }

    private val visualizerView: SoundVisualizerView by lazy {
        findViewById(R.id.visualizerView)
    }

    private val recordTimeTextView: CountUpTextView by lazy {
        findViewById(R.id.recordTimeTextView)
    }

    private var state = State.BEFORE_RECORDING
        set(value) {
            field = value
            resetBtn.isEnabled = (state == State.AFTER_RECORDING) || (state == State.ON_PLAYING)
            recordBtn.updateIconWithState(value)
        }

    private var recorder: MediaRecorder? = null

    private var player: MediaPlayer? = null

    private val requiredPermissions = arrayOf(Manifest.permission.RECORD_AUDIO);

    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

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
        if (!audioRecordPermissionGranted) {
            finish()
        }

    }

    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
        initView()
        bindViews()
    }

    private fun initView() {
        state = State.BEFORE_RECORDING
        recordBtn.updateIconWithState(state)
    }

    private fun bindViews() {
        visualizerView.onRequestCurrentAmp = {
            recorder?.maxAmplitude ?: 0
        }

        resetBtn.setOnClickListener {
            stopPlaying()
            visualizerView.clearVisualizing()
            recordTimeTextView.clearCountUp()
            state = State.BEFORE_RECORDING
        }
        recordBtn.setOnClickListener {
            when (state) {
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }

    }


    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()
        visualizerView.startVisualizing(false)
        recordTimeTextView.startCountUp()
        state = State.ON_RECORDING
    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        visualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare()
        }
        player?.setOnCompletionListener {
            stopPlaying()
        }
        player?.start()
        visualizerView.startVisualizing(true)
        recordTimeTextView.startCountUp()
        state = State.ON_PLAYING
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        visualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201;
    }
}