package com.example.studyApp.animation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieTask;
import com.example.studyApp.R;

public class LottieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie);
       LottieAnimationView lottieAnimationView = findViewById(R.id.lottie_animation_view);
       lottieAnimationView.setRepeatCount(-1);
        LottieCompositionFactory.fromAsset(this,"/Users/a58/gzpProjects/AndroidStudioProjects/lottie/app/asset/lottiljson.json");
       //lottieAnimationView.setAnimation("lottiljson.json");

        LottieComposition cacheAnim = LottieCompositionFactory.fromAssetSync(this, "lottiljson.json", "lottiljson.json").getValue();

        LottieTask<LottieComposition> cacheAnim2= LottieCompositionFactory.fromAsset(this,"lottiljson.json");
        lottieAnimationView.setComposition(cacheAnim);
     //   lottieAnimationView.setAnimation("lottiljson.json");
        lottieAnimationView.playAnimation();

    }
}