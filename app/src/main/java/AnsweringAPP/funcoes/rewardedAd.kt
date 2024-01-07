package AnsweringAPP.funcoes

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.Toast
import AnsweringAPP.activities.beginner
import AnsweringAPP.dados.COINS
import AnsweringAPP.dados.TABLE_NAME
import com.AnsweringAPP.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


private var rewardvar: RewardedAd? = null

class rewardedAd {
    private final var TAG = "MainActivity"
    fun loadReward(ctx : Context) {


        var adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            ctx,
            "ca-app-pub-2884509228034182/7941879672",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError?.message?.let { Log.d(TAG, it) }
                    rewardvar = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d(TAG, "Ad was loaded.")
                    rewardvar = rewardedAd
                }
            })


    }
fun  showAd(ctx : Activity, db : SQLiteDatabase) {
    if (rewardvar != null) {
        rewardvar?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad was shown.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                // Called when ad fails to show.
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad was dismissed.")
                rewardvar = null
                this@rewardedAd.loadReward(ctx)
            }
        }
        rewardvar?.show(ctx, OnUserEarnedRewardListener() {
            fun onUserEarnedReward(rewardItem: RewardItem) {
                var cv = ContentValues()
                val selectQuery = "SELECT * FROM $TABLE_NAME;"
                val cursor = db.rawQuery(selectQuery, null)
                cursor.moveToFirst()
                var result = cursor.getString(2).toInt()
                var update =  result + 3
                cursor.close()

                Log.d(TAG, "Ad was finished. User earned the reward.")

                cv.put(COINS, update)
                db.update(TABLE_NAME, cv,null,null)
                Toast.makeText(ctx,ctx.getString(R.string.coin) + ": +3",Toast.LENGTH_SHORT).show()
                beginner().loadCoins(ctx,db)
            }
            onUserEarnedReward(it)
        })
    } else {
        Toast.makeText(ctx,"Wait a little, or verify your internet connection.",Toast.LENGTH_SHORT).show()
        Log.d(TAG, "The rewarded ad wasn't ready yet.")
    }
}



}
