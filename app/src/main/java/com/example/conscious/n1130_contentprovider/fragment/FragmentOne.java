package com.example.conscious.n1130_contentprovider.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.conscious.n1130_contentprovider.R;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by conscious on 2016-12-06.
 */

public class FragmentOne extends BaseFragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_IMAGE_CROP = 3;

    private Uri photoURI, albumURI = null;
    private Boolean album = false;

    private Button btn1;
    private Button btn2;
    private ImageView imageView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn1 = (Button) view.findViewById(R.id.button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        btn2 = (Button) view.findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        imageView = (ImageView) view.findViewById(R.id.imageView);
    }


    // 사진찍기
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            File file = null;
            try {
                file = createImageFile(); // 사진찍은 후 저장할 임시 파일
            } catch (IOException ex) {
                Toast.makeText(getContext(), "createImageFile Failed", Toast.LENGTH_LONG).show();
            }

            if (file != null) {
                photoURI = Uri.fromFile(file); // 임시 파일의 위치,경로 가져옴
                albumURI = Uri.fromFile(file); // 임시 파일의 위치,경로 가져옴
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI); // 임시 파일 위치에 저장
                getActivity().startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // 저장할 폴더 생성
    private File createImageFile() throws IOException {
//         Create an image file name, 폴더명 지정 방법 (문제 : DIRECTORY_DCIM , DIRECTORY_PICTURE 경로가 없는 폰 존재)
//        String imageFileName = "tmp_" + String.valueOf(System.currentTimeMillis());
//        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/APPmyTEST");
//        File file = File.createTempFile(imageFileName, ".jpg", storageDir);
//        mCurrentPhotoPath = file.getAbsolutePath();
//        return file;

//
//        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel";
//        File directory_SmartWheel = new File(dirPath);
//
//        if(!directory_SmartWheel.exists()) // SmartWheel 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
//            directory_SmartWheel.mkdir();

//         특정 경로와 폴더를 지정하지 않고, 메모리 최상 위치에 저장 방법
        String imgName = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory(), imgName);
//        mCurrentPhotoPath = storageDir.getAbsolutePath();
        return file;
    }

    private void openGallery() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        getActivity().startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    private void cropImage() {
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(photoURI, "image/*");
        intent.putExtra("outputX", 500); // crop한 이미지의 x축 크기
        intent.putExtra("outputY", 500); // crop한 이미지의 y축 크기
        intent.putExtra("aspectX", 1); // crop 박스의 x축 비율
        intent.putExtra("aspectY", 1); // crop 박스의 y축 비율
        intent.putExtra("scale", true);

        if (album == false) {
            intent.putExtra("output", photoURI); // 크랍된 이미지를 해당 경로에 저장
        } else if (album == true) {
            intent.putExtra("output", albumURI); // 크랍된 이미지를 해당 경로에 저장
        }


        getActivity().startActivityForResult(intent, REQUEST_IMAGE_CROP);
    }

    @Subscribe
    public void onActivityResult(ActivityResultEvent are) {
//        onActivityResult(are.getRequestCode(), are.getResultCode(), are.getData());
        Toast.makeText(getContext(), "onActivityResult~~~", Toast.LENGTH_SHORT).show();

        if (are.getResultCode() != RESULT_OK) {
            Toast.makeText(getContext(), "onActivityResult : RESULT_NOT_OK", Toast.LENGTH_LONG).show();
        } else {
            switch (are.getResultCode()) {
                case REQUEST_TAKE_PHOTO: // 앨범 이미지 가져오기
                    Toast.makeText(getContext(), "REQUEST_TAKE_PHOTO", Toast.LENGTH_SHORT).show();
                    album = true;
                    File file = null;
                    try {
                        file = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (file != null) {
                        albumURI = Uri.fromFile(file); // 앨범 이미지 Crop한 결과는 새로운 위치 저장
                    }

                    photoURI = are.getData().getData(); // 앨범 이미지의 경로
//                    photoURI = albumURI; // 앨범 이미지의 경로

                    // break; REQUEST_IMAGE_CAPTURE로 전달하여 Crop
                case REQUEST_IMAGE_CAPTURE:

                    cropImage();

                    break;
                case REQUEST_IMAGE_CROP:
                    Toast.makeText(getContext(), "REQUEST_IMAGE_CROP", Toast.LENGTH_SHORT).show();


//                    Bitmap bitmap = BitmapFactory.decodeFile(photoURI.getPath());
//                    iv_capture.setImageBitmap(bitmap);

                    uploadImage();

//                    Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE ); // 동기화
//                    if(album == false) {
//                        mediaScanIntent.setData(photoURI); // 동기화
//                    } else if(album == true){
//                        album = false;
//                        mediaScanIntent.setData(albumURI); // 동기화
//                    }
//                    this.sendBroadcast(mediaScanIntent); // 동기화

                    break;
            }
        }
    }

    private void uploadImage() {

//                     iv_capture 에 띄우기
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), albumURI);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
    }
}
