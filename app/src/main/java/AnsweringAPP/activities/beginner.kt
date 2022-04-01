package AnsweringAPP.activities

import AnsweringAPP.dados.*
import AnsweringAPP.funcoes.Translate
import AnsweringAPP.funcoes.rewardedAd
import AnsweringAPP.funcoes.textToSpeak
import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi

import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.AnsweringAPP.R
import com.AnsweringAPP.databinding.BeginnerBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.hbisoft.hbrecorder.HBRecorder
import com.hbisoft.hbrecorder.HBRecorderListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

private lateinit var binding: BeginnerBinding
class beginner : AppCompatActivity(), HBRecorderListener {
    private var interstitialAd: InterstitialAd? = null
    private val bannerAd : AdView? = null
    var hbRecorder: HBRecorder? = null
    var btnStart: Button? = null
    var hasPermissions = false
    var contentValues: ContentValues? = null
    var resolver: ContentResolver? = null
    var mUri: Uri? = null

    //cameraX
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
//    private val cameraProviderResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permissionGranted->
//        if(permissionGranted){
//            // cut and paste the previous startCamera() call here.
//            startCameraPreview()
//        }else {
//            Snackbar.make(binding.root,"The camera permission is required", Snackbar.LENGTH_INDEFINITE).show()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BeginnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

//        private val REQUIRED_PERMISSIONS = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        )
//        requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        //CAMERA PREVIEW
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA


        //Traduções eram clicando nos botões, agora são automáticas
//        binding.cxTexto.setOnClickListener {
//            Translate().question(binding.cxTexto,binding.cxTradQ,this)
//        }
//        binding.cxHint.setOnClickListener {
//            Translate().hint(binding.cxHint,binding.cxTradH,this)
//        }

        //==========DATABASE-MYSQL==============
        val mainClass = this
        val classdb = localSqlDatabase(mainClass)
        val db = classdb.writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME;"
        val cursor = db.rawQuery(selectQuery, null)
        cursor.moveToFirst()
        fun getInt(coluna: String):Int{ return cursor.getInt(cursor.getColumnIndexOrThrow(coluna)) }

        //====CARREGANDO MOEDAS==========

        loadCoins(this,db)

        //===========AD-MOB=========
        MobileAds.initialize(this) {}
        //loadInterstitial()
        loadBanner()
        rewardedAd().loadReward(this)


        //========BT-AD-REWARD========
        binding.btReward.setOnClickListener {
            rewardedAd().showAd(this,db)
            loadCoins(this,db)

        }




        //===========CHAMANDO PERGUNTAS e DEFININDO O TIMER==============
        var question_timer : Int
        var questions = Questions(this)
        var selectedLevel : Array<List<String>>
        var level:String = intent.getStringExtra("level").toString()
        when{
            level == "basic" -> {
                selectedLevel = questions.Basic
                question_timer = getInt(TM_BASIC)
            }

            level == "intermediate" -> {
                selectedLevel = questions.Intermediate
                question_timer = getInt(TM_INTERM)
            }
            level == "advanced" -> {
                selectedLevel = questions.Advanced
                question_timer = getInt(TM_ADVANC)
            }
            level == "begInterm" -> {
                selectedLevel = questions.begInterm
                question_timer = getInt(TM_BEG_INTERM)
            }
            level == "allquestions" -> {
                selectedLevel = questions.allLevels
                question_timer = getInt(TM_ALLQUESTIONS)
            }
            else -> {
                selectedLevel = questions.Basic
                question_timer = 9
            }
        }
        //==========DEFININDO O TIMER===================
        if (question_timer <= 4){question_timer = 5}
        var timer = (question_timer * 1000).toLong()

        //EMBARALHANDO PERGUNTAS
        selectedLevel.shuffle()

        //==========INICIANDO FUNÇÕES DA TELA DE PERGUNTAS===============
        textToSpeak.TTS(this,selectedLevel, binding.cxTexto, binding.cxHint,applicationContext, binding.playquestion,
            binding.playhint, binding.checkDicas, binding.btFront, binding.btBack, binding.btAutomatic,db,timer,
            binding.cxTradQ,
            binding.cxTradH)

        //STARTING SCREEN RECORDER AND CAMERA PREVIEW
        hbRecorder = HBRecorder(this, this)
        hbRecorder!!.setVideoEncoder("H264")
        binding.toggle?.setOnClickListener{
            if (binding.toggle?.isChecked == true){
                //first check if permissions was granted
                if (checkSelfPermission(
                        Manifest.permission.RECORD_AUDIO,
                        PERMISSION_REQ_ID_RECORD_AUDIO
                    ) && checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE
                    ) && checkSelfPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
                ) {
                    hasPermissions = true
                }
                if (hasPermissions) {
                    binding.preview?.visibility = View.VISIBLE
                    binding.adView?.visibility = View.INVISIBLE
                    binding.adView2?.visibility = View.INVISIBLE
                    binding.btAutomatic?.visibility = View.INVISIBLE
                    binding.btReward?.visibility = View.INVISIBLE
                    binding.txtMoedas?.visibility = View.INVISIBLE
                    binding.contanersss?.setBackgroundColor(getColor(R.color.blackInvisible))

                    startCameraPreview()
                    startRecordingScreen()
                }else {
                    Translate(this).toastTrad("The app need camera & audio permissions, verify the app configurations")
//                    if (requestPermission()[0] != PackageManager.PERMISSION_GRANTED || requestPermission()[1] != PackageManager.PERMISSION_GRANTED || requestPermission()[2] != PackageManager.PERMISSION_GRANTED){
//                        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
//                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                            intent.data = Uri.parse("package:$packageName")
//                            startActivity(intent)
//                    }
                    binding.toggle!!.isChecked = false
                                    }
            }else{
                hbRecorder!!.stopScreenRecording()
                binding.preview?.visibility = View.GONE
                binding.adView?.visibility = View.VISIBLE
                binding.adView2?.visibility = View.VISIBLE
                binding.btAutomatic?.visibility = View.VISIBLE
                binding.btReward?.visibility = View.VISIBLE
                binding.txtMoedas?.visibility = View.VISIBLE
                binding.contanersss?.setBackgroundColor(getColor(R.color.blackTr))
                binding.contanersss?.setBackgroundResource(R.drawable.container_components)

            }
        }



    }
    public override fun onPause() {
        binding.adView.pause()
        binding.adView2?.pause()
        super.onPause()
    }

    // Called when returning to the activity
    public override fun onResume() {
        super.onResume()
        binding.adView.resume()
        binding.adView2?.resume()
    }

    // Called before the activity is destroyed
    public override fun onDestroy() {
        binding.adView.destroy()
        binding.adView2?.destroy()
        super.onDestroy()
    }

    fun loadCoins(ctx: Context, db : SQLiteDatabase): Int {
        val selectQuery = "SELECT * FROM $TABLE_NAME;"
        val cursor = db.rawQuery(selectQuery, null)
        cursor.moveToFirst()
        var result = cursor.getString(2).toInt()
        binding.txtMoedas.text = result.toString()
        cursor.close()
        return result

    }

    private fun loadBanner() {
        val adRequest = AdRequest.Builder().build()
        binding.adView2?.loadAd(adRequest)
        binding.adView.loadAd(adRequest)
//      real banner  ca-app-pub-2884509228034182/8623568236
//     teste   ca-app-pub-3940256099942544/6300978111
    }
//real: ca-app-pub-2884509228034182/7941879672
    //teste: ca-app-pub-3940256099942544/8691691433
    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-2884509228034182/7941879672",adRequest,object :InterstitialAdLoadCallback(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                interstitialAd = null
            }

            override fun onAdLoaded(p0: InterstitialAd) {
                super.onAdLoaded(p0)
                interstitialAd = p0
            }
        }


        )
    }

    fun showInterstitial(view : View){
        interstitialAd?.show(this)
    }

    override fun HBRecorderOnStart() {

    }

    override fun HBRecorderOnComplete() {
        //Update gallery depending on SDK Level
        if (hbRecorder!!.wasUriSet()) {
            updateGalleryUri()
        } else {
            refreshGalleryFile()
        }
        Translate(this).toastTrad("Your video has been saved in the gallery.")
    }

    override fun HBRecorderOnError(errorCode: Int, reason: String) {
        Toast.makeText(this, "$errorCode: $reason", Toast.LENGTH_SHORT).show()
    }

    private fun startRecordingScreen() {
        val mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val permissionIntent = mediaProjectionManager?.createScreenCaptureIntent()
        startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCREEN_RECORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Start screen recording
                hbRecorder!!.startScreenRecording(data, resultCode, this)
            }
        }
    }

    //For Android 10> we will pass a Uri to HBRecorder
    //This is not necessary - You can still use getExternalStoragePublicDirectory
    //But then you will have to add android:requestLegacyExternalStorage="true" in your Manifest
    //IT IS IMPORTANT TO SET THE FILE NAME THE SAME AS THE NAME YOU USE FOR TITLE AND DISPLAY_NAME
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun setOutputPath() {
        val filename = generateFileName()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver = contentResolver
            contentValues = ContentValues()
            contentValues!!.put(MediaStore.Video.Media.RELATIVE_PATH, "SpeedTest/" + "SpeedTest")
            contentValues!!.put(MediaStore.Video.Media.TITLE, filename)
            contentValues!!.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            contentValues!!.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            mUri = resolver?.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            //FILE NAME SHOULD BE THE SAME
            hbRecorder!!.fileName = filename
            hbRecorder!!.setOutputUri(mUri)
        } else {
            createFolder()
            hbRecorder!!.setOutputPath(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                    .toString() + "/HBRecorder"
            )
        }
    }

    //Check if permissions was granted
    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            return false
        }
        return true
    }

    private fun updateGalleryUri() {
        contentValues!!.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues!!.put(MediaStore.Video.Media.IS_PENDING, 0)
        }else{

        }
        contentResolver.update(mUri!!, contentValues, null, null)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun refreshGalleryFile() {
        MediaScannerConnection.scanFile(
            this, arrayOf(hbRecorder!!.filePath), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
    }

    //Generate a timestamp to be used as a file name
    private fun generateFileName(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
        val curDate = Date(System.currentTimeMillis())
        return formatter.format(curDate).replace(" ", "")
    }

    //drawable to byte[]
    private fun drawable2ByteArray(@DrawableRes drawableId: Int): ByteArray {
        val icon = BitmapFactory.decodeResource(resources, drawableId)
        val stream = ByteArrayOutputStream()
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    //Create Folder
    //Only call this on Android 9 and lower (getExternalStoragePublicDirectory is deprecated)
    //This can still be used on Android 10> but you will have to add android:requestLegacyExternalStorage="true" in your Manifest
    private fun createFolder() {
        val f1 = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            "SpeedTest"
        )
        if (!f1.exists()) {
            if (f1.mkdirs()) {
                Log.i("Folder ", "created")
            }
        }
    }

    companion object {
        private const val SCREEN_RECORD_REQUEST_CODE = 100
        private const val PERMISSION_REQ_ID_RECORD_AUDIO = 101
        private const val PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = 102
        private const val CAMERA_PERMISSION_CODE = 103
    }
    private fun startCameraPreview(){
        // listening for data from the camera
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // connecting a preview use case to the preview in the xml file.
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(binding.preview?.surfaceProvider)
            }
            try{
                // clear all the previous use cases first.
                cameraProvider.unbindAll()
                // binding the lifecycle of the camera to the lifecycle of the application.
                cameraProvider.bindToLifecycle(this,cameraSelector,preview)
            } catch (e: Exception) {

            }

        }, ContextCompat.getMainExecutor(this))
    }
    private fun requestPermission(): List<Int> {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                //Permission is denied
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            } else {
                //ask permission

            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Permission is denied
            } else {
                //ask permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE)
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                //Permission is denied
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQ_ID_RECORD_AUDIO)
            } else {
                //ask permission

            }
        }
Toast.makeText(this,"tentando",Toast.LENGTH_SHORT).show()
    return listOf(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO),ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE),ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA))
    }

}