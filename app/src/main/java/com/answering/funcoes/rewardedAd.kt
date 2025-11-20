package com.answering.funcoes

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.Toast
import com.answering.activities.beginner
import com.answering.dados.COINS
import com.answering.dados.TABLE_NAME
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


private var rewardvar: RewardedAd? = null

class rewardedAd {
    private val TAG = "rewardedAd"

    // helper to get string resource by name without referencing R directly
    private fun Context.getStringByName(name: String): String {
        val id = this.resources.getIdentifier(name, "string", this.packageName)
        return if (id != 0) this.getString(id) else name
    }

    fun loadReward(ctx : Context) {

        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            ctx,
            "ca-app-pub-2884509228034182/7941879672",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
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

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
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
            // Use the OnUserEarnedRewardListener from com.google.android.gms.ads (newer SDK)
            rewardvar?.show(ctx, object : com.google.android.gms.ads.OnUserEarnedRewardListener {
                override fun onUserEarnedReward(rewardItem: com.google.android.gms.ads.rewarded.RewardItem) {
                    val cv = ContentValues()
                    val selectQuery = "SELECT * FROM $TABLE_NAME;"
                    val cursor = db.rawQuery(selectQuery, null)
                    cursor.moveToFirst()
                    val result = cursor.getString(2).toInt()
                    val update =  result + 3
                    cursor.close()

                    Log.d(TAG, "Ad was finished. User earned the reward.")

                    cv.put(COINS, update)
                    db.update(TABLE_NAME, cv,null,null)
                    Toast.makeText(ctx, ctx.getStringByName("coin") + ": +3", Toast.LENGTH_SHORT).show()
                    beginner().loadCoins(ctx,db)
                }
            })
        } else {
            Toast.makeText(ctx,"Wait a little, or verify your internet connection.",Toast.LENGTH_SHORT).show()
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }


}
