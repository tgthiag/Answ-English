package AnsweringAPP.activities

import AnsweringAPP.dados.*
import AnsweringAPP.funcoes.*
import android.Manifest
import android.app.Activity
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
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.AnsweringAPP.BuildConfig
import com.AnsweringAPP.R
import com.AnsweringAPP.databinding.BeginnerBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BeginnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

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
        var cursor = db.rawQuery(selectQuery, null)
        cursor.moveToFirst()

        //get Timer between questions
        fun getInt(coluna: String):Int{ return cursor.getInt(cursor.getColumnIndexOrThrow(coluna)) }

        //Update Days using and Daily coins
        DailyCoins().UpdateDayAndCoins(db,this)

        Instructions(this).firstAcess(db)
        binding.btInstructions.setOnClickListener {
            Instructions(this).callInstructions()
        }

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

        //Chamando texto "days using" na record screen.
        var usoDoApp = binding.txtUsingApp?.text.toString()
        cursor = db.rawQuery(selectQuery, null)
        cursor.moveToFirst()
        var daysUsing = cursor.getString(11)
        if (daysUsing > "1"){
            var usoDoAppFinal = usoDoApp.replace("#", daysUsing)
            binding.txtUsingApp?.text = usoDoAppFinal
        }else{binding.txtUsingApp?.text = getString(R.string.first_day)}

        //Traduzindo botões e widgets para o idioma do User, caso não existam traduções disponíveis
        Translate(this).translateButtons(binding.btReward, binding.btAutomatic, binding.checkDicas,
            binding.playquestion, binding.playhint, binding.txtUsingApp
        )

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
        binding.toggle.setOnClickListener{
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
                    loadCoins(this,db)
                    cursor = db.rawQuery(selectQuery, null)
                    cursor.moveToFirst()
                    var coinsQtd = cursor.getString(2).toInt()
                    if (coinsQtd >= 1) {
                        binding.preview?.visibility = View.VISIBLE
                        binding.adView?.visibility = View.INVISIBLE
                        binding.adView2?.visibility = View.INVISIBLE
                        binding.adCam?.visibility = View.VISIBLE
                        binding.btAutomatic?.visibility = View.INVISIBLE
                        binding.btReward?.visibility = View.INVISIBLE
                        binding.btInstructions?.visibility = View.INVISIBLE
                        binding.txtMoedas?.visibility = View.INVISIBLE
                        binding.txtUsingApp?.visibility = View.VISIBLE
                        binding.contanersss?.setBackgroundColor(getColor(R.color.blackInvisible))
                        hideSystemUI()


                        startCameraPreview()
                        startRecordingScreen()
                    }else{
                        try {
                            DialogShow().DialogCustom(this,"You don't have coins.\n\nYou will earn 2 coin every day that you open the app.\n\n Also, you can watch a video to earn 3 coins anytime.")
                        }catch (e: Exception){
                            print("erro")}
                        binding.toggle.isChecked = false
                    }
                }else {
                    Translate(this).toastTrad("The app need camera & audio permissions, verify the app configurations")
                    requestPermission()
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
                binding.adCam?.visibility = View.GONE
                binding.btAutomatic?.visibility = View.VISIBLE
                binding.btReward?.visibility = View.VISIBLE
                binding.btInstructions?.visibility = View.VISIBLE
                binding.txtMoedas?.visibility = View.VISIBLE
                binding.txtUsingApp?.visibility =View.INVISIBLE
                binding.contanersss?.setBackgroundColor(getColor(R.color.blackTr))
                binding.contanersss?.setBackgroundResource(R.drawable.container_components)
                showSystemUI()

            }
        }



    }
    fun Activity.hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                // Default behavior is that if navigation bar is hidden, the system will "steal" touches
                // and show it again upon user's touch. We just want the user to be able to show the
                // navigation bar by swipe, touches are handled by custom code -> change system bar behavior.
                // Alternative to deprecated SYSTEM_UI_FLAG_IMMERSIVE.
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                // make navigation bar translucent (alternative to deprecated
                // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                // - do this already in hideSystemUI() so that the bar
                // is translucent if user swipes it up
                window.navigationBarColor = getColor(R.color.whiteTr)
                // Finally, hide the system bars, alternative to View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                // and SYSTEM_UI_FLAG_FULLSCREEN.
                it.hide(WindowInsets.Type.systemBars())
            }
        } else {
            // Enables regular immersive mode.
            // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
            // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    // Do not let system steal touches for showing the navigation bar
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Hide the nav bar and status bar
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            // Keep the app content behind the bars even if user swipes them up
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            // make navbar translucent - do this already in hideSystemUI() so that the bar
            // is translucent if user swipes it up
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    fun Activity.showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // show app content in fullscreen, i. e. behind the bars when they are shown (alternative to
            // deprecated View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.setDecorFitsSystemWindows(false)
            // finally, show the system bars
            window.insetsController?.show(WindowInsets.Type.systemBars())
        } else {
            // Shows the system bars by removing all the flags
            // except for the ones that make the content appear under the system bars.
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }
    public override fun onPause() {
        binding.adView.pause()
        binding.adView2?.pause()
        super.onPause()
    }
    public override fun onStop() {
        binding.adView.pause()
        binding.adView2?.pause()
        super.onStop()
        if (hbRecorder!!.isBusyRecording){
            hbRecorder!!.stopScreenRecording()
        }
    }

    public override fun onRestart() {
        super.onRestart()
        binding.adView.resume()
        binding.adView2?.resume()
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
    fun useCoin(){
        val mainClass = this@beginner
        val classdb = localSqlDatabase(mainClass)
        val db = classdb.writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME;"
        val cursores = db.rawQuery(selectQuery, null)
        cursores.moveToFirst()
        var moedaAtual = cursores.getString(2).toInt()
        var moedaResult = moedaAtual -1
        var cv = ContentValues()
        cv.put(COINS,moedaResult)
        db.update(TABLE_NAME,cv,null,null)
        loadCoins(mainClass,db)
        cursores.close()
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

        Question().readByVoice(this, binding.cxTexto)
    }

    override fun HBRecorderOnComplete() {
        //Update gallery depending on SDK Level

        if (hbRecorder!!.wasUriSet()) {
            updateGalleryUri()
        } else {
            refreshGalleryFile()
        }
        useCoin()
        val mainClass = this
        val classdb = localSqlDatabase(mainClass)
        val db = classdb.writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME;"
        var cursoress = db.rawQuery(selectQuery, null)
        cursoress.moveToFirst()
        var usedTheApp = cursoress.getString(4).toInt()
        var moedas = cursoress.getString(2).toInt()
        if (usedTheApp == 1 && moedas <= 4){
            try {
                DialogShow().DialogReview(this,db,"Are you enjoying this app?")
            }catch (e: Exception){
                print("erro")}
        }
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
        Toast.makeText(this,mUri.toString(),Toast.LENGTH_LONG).show()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun refreshGalleryFile() {
        MediaScannerConnection.scanFile(
            this, arrayOf(hbRecorder!!.filePath), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
            try {
                shareVideo(path)
            }catch (e: Exception){
                print("erro")}
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
        return listOf(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO),ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE),ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA))
    }
    fun shareVideo(filePath:String) {

        val videoFile = File(filePath)
        val videoURI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID + ".fileprovider", videoFile) //baseContext.packageName
        else
            Uri.fromFile(videoFile)
        ShareCompat.IntentBuilder.from(this)
            .setStream(videoURI)
            .setType("video/mp4")
            .setChooserTitle("Share your video to social media")
            .startChooser()
    }
}