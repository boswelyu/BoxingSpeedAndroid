package com.tealcode.boxingspeed.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.edmodo.cropper.CropImageView;
import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.utility.ImageUtil;

/**
 * Created by YuBo on 2017/10/23.
 */

public class CropImageActivity extends Activity{

    private CropImageView mCropImageView;
    private Button        mSaveImageBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crop_image);

        mCropImageView = (CropImageView) findViewById(R.id.crop_image_view);
        mSaveImageBtn = (Button) findViewById(R.id.save_crop_image_btn);

        mCropImageView.setAspectRatio(10, 10);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setGuidelines(CropImageView.GUIDELINES_ON_TOUCH);


        Intent intent = getIntent();
        Uri sourceUri = intent.getData();
        Bitmap bitmap = ImageUtil.getBitmapFromUri(this, sourceUri);
        mCropImageView.setImageBitmap(bitmap);

        mSaveImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap cropBitmap = mCropImageView.getCroppedImage();

                Uri cropUri = ImageUtil.saveBitmapToUri(CropImageActivity.this, cropBitmap, "upload", "crop_avatar.jpg");

                Intent intent = new Intent();
                intent.setData(cropUri);

                setResult(RESULT_OK, intent);

                finish();
            }
        });

    }
}
