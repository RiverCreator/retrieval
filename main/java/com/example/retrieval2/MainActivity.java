package com.example.retrieval2;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.retrieval2.listener.MyOkListiner;
import com.example.retrieval2.utils.HttpUtils;
import com.example.retrieval2.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static android.os.FileUtils.copy;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button camera;
    private Button up_pic;
    private ImageView showImg_CropImg;
    LinearLayout background;
    private Uri imageUri;
    private Uri Crop_Uri;
    private Uri Gallery_Uri;
    private String picturePath;
    private static final int CAMERA_REQUEST = 104;
    private static final int GALLERY_REQUEST = 103;
    private static final int RESULT_REQUEST_CODE = 102;
    private Button up;
    private Uri CropPath;
    private Module module;
    private String json_results;
    //String path = this.getExternalFilesDir(null) + File.separator + "Pictures" + File.separator + "tempfile/";
    String path;
    //String path=this.getExternalFilesDir(null)+"/files/Pictures/";
    File mCameraFile;
    File mCropFile;
    File mGalleryFile;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int REQUEST_CODE_CONTACT = 101;
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar();        //隐藏标题栏
        if (actionbar != null) {
            actionbar.hide();
        }
        //camera=(Button)findViewById(R.id.camera);
        up_pic = (Button) findViewById(R.id.up_image);
        background = (LinearLayout) findViewById(R.id.background);
        showImg_CropImg = (ImageView) findViewById(R.id.showimg);
        background.getBackground().setAlpha(125);
        //camera.setOnClickListener(cameraClickListener);
        //up_pic.setOnClickListener(upClickListener);
        //showImg_CropImg.setOnClickListener(CropClickListener);
        up = (Button) findViewById(R.id.up);
        camera = findViewById(R.id.camera);
        camera.setOnClickListener(this);
        up_pic.setOnClickListener(this);
        up.setOnClickListener(this);
        try {
            //module=Module.load(Utils.assetFilePath(this, "model-script.pt"));
            module=Module.load(Utils.assetFilePath(this, "model-script-resnet3.pt"));
            System.out.println("Succeed load model ...");
        } catch (IOException e) {
            Log.e("Pytorch ClS", "Error reading assets", e);
            e.printStackTrace();
        }
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
        //验证是否许可权限
        for (String str : permissions) {
            if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
            }
        }
        path=Environment.getExternalStorageDirectory()+"/Android/data/com.example.retrieval2/files/Pictures";
        String Picpath = MainActivity.this.getExternalFilesDir(null).getAbsolutePath() + File.separator + "Pictures";
        File Picstorage=new File(Picpath);
        boolean testbool=false;
        if(!Picstorage.exists())
            testbool=Picstorage.mkdirs();
        mCameraFile = new File(path, "IMAGE_FILE_NAME.jpg");//照相机的File对象
        mCropFile = new File(path, "PHOTO_FILE_NAME.jpg");//裁剪后的File对象
        mGalleryFile = new File(path, "IMAGE_GALLERY_NAME.jpg");//相册的File对象
        //initView();
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultcode", resultCode + "");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //拍照结束后返回
                case CAMERA_REQUEST:
                    Uri inputUri = FileProvider.getUriForFile(this, "com.example.retrieval2.fileprovider", mCameraFile);
                    startPhotoZoom(inputUri);
                    break;
                case GALLERY_REQUEST:
                    Gallery_Uri=data.getData();
                    startPhotoZoom(data.getData());
//                    Bitmap bitmap2 = data.getParcelableExtra("data");
//                    showImg_CropImg.setImageBitmap(bitmap2);
                    //裁剪完图片设置显示
                case RESULT_REQUEST_CODE:
                    Uri inputUri2=FileProvider.getUriForFile(this, "com.example.retrieval2.fileprovider", mCropFile);
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(inputUri2));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //Bitmap bitmap = data.getParcelableExtra("data");
                    showImg_CropImg.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera: {//摄像头拍照上传
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用摄像头
                File imageFile=null;
                String storagePath;
                File storageDir;
                String IMAGE_NAME = "temp";
                storagePath = MainActivity.this.getExternalFilesDir(null).getAbsolutePath() + File.separator + "Pictures" + File.separator + "tempfile";
                storageDir = new File(storagePath);
                boolean bool=false;
                if(!storageDir.exists())
                    bool=storageDir.mkdirs();
                try {
                    imageFile = File.createTempFile(IMAGE_NAME, ".jpg", storageDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = FileProvider.getUriForFile(this, "com.example.retrieval2.fileprovider", mCameraFile);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
//                Bundle bundle=new Bundle();
//                bundle.putString("tempuri",tempuri.toString());
//                intent.putExtras(bundle);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, CAMERA_REQUEST);
                break;
            }
            case R.id.up_image:{//选择图库获取图片
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
                break;
            }
            case R.id.up:{//点击上传
                String[] filePathColumn={MediaStore.Images.Media.DATA};
                if(Crop_Uri!=null){
                    Bitmap real_Img=ImageSizeCompress(Uri.fromFile(mCropFile));
                    float[] mean = new float[]{0.485f, 0.456f, 0.406f};   //offset to {104.0f, 117.0f, 123.0f}
                    float[] std = new float[]{0.229f, 0.224f, 0.225f};
                    final Tensor inputTensor = Utils.bitmapToFloat32Tensor(real_Img,
                            mean, std);
                    System.out.println(inputTensor.getDataAsFloatArray().length);
                    long startTime = System.currentTimeMillis();
                    final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
                    final float[] mytensor=outputTensor.getDataAsFloatArray();
                    long endTime = System.currentTimeMillis();
                    System.out.println("程序运行时间：" + (endTime - startTime) / 2 + "ms");
                    long infertime = (endTime - startTime) / 2;
                    System.out.println("Out put length : " + outputTensor.getDataAsFloatArray().length);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("mytensor", Arrays.toString(mytensor));
                    Toast.makeText(MainActivity.this,"查询中，请稍等...",Toast.LENGTH_SHORT).show();
                    HttpUtils.getInstance().dopost("http://192.168.43.92:8880/myfunc2", map, new MyOkListiner() {
                        @Override
                        public void onOK(String json) throws JSONException {
                            json_results=json;
                            Log.i("posttag", "onOK: "+json);
                            //Looper.prepare();
                            //Toast.makeText(MainActivity.this,"查询成功！",Toast.LENGTH_SHORT).show();
                            JSONObject result_json=new JSONObject(json);
                            //JSONArray item=result_json.getJSONArray("0");
                            for(int i=0;i<20;i++){
                                JSONObject data=result_json.getJSONObject(Integer.toString(i));
                                Log.i("posttag", "getmsg: "+data.getString("id"));
                            }
                            Log.i("posttag", "getmsg: "+json_results);
                            Intent intent = new Intent(MainActivity.this, retrieval_out.class);
                            intent.putExtra("results",json_results);
                            startActivity(intent);
                            //Looper.loop();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(MainActivity.this,"服务器连接失败！",Toast.LENGTH_SHORT).show();
                            Log.i("posttag", "onfail: "+message);
                        }
                    });
//                    Log.i("posttag", "getmsg: "+json_results);
//                    Intent intent = new Intent(this, retrieval_out.class);
//                    intent.putExtra("results",json_results);
                    //startActivity(intent);
                }
                break;

            }
        }
    }

    //裁剪图片
    public void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.e("TAG", "The uri is not exist.");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");//调动系统裁剪
        intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION | FLAG_GRANT_READ_URI_PERMISSION| FLAG_GRANT_READ_URI_PERMISSION);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            String url = getPath(this.getApplication(), uri);//获取图片的完整路径
//            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
//        } else {
//            intent.setDataAndType(uri, "image/*");
//        }
        // 设置裁剪
        String FilePath = this.getExternalFilesDir(null).getAbsolutePath() + File.separator + "Pictures" + File.separator + "tempfile/"+"crop_temp.jpg";
        File outputFile =new File(FilePath);
        if (outputFile.exists()){
            outputFile.delete();
            outputFile =new File(FilePath);
        }
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdir();
        }
        Uri outputUri=Uri.fromFile(mCropFile);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
        Crop_Uri=FileProvider.getUriForFile(this, "com.example.retrieval2.fileprovider", outputFile);

        //设置裁剪内容
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        //intent.putExtra("aspectX", 1);
        //intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 224);
        intent.putExtra("outputY", 224);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", "JPEG");
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
        //intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(outputFile));
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    public String saveBitmap(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Toast.makeText(this, "内存卡异常，请检查内存卡插入是否正确", Toast.LENGTH_SHORT).show();
            return "";
        }
        String path = System.currentTimeMillis() + ".jpg";
        File f = new File(Environment.getExternalStorageDirectory() + "/myphoto/", path);
        createFile();
        try {
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return f.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private Bitmap ImageSizeCompress(Uri uri){
        InputStream Stream = null;
        InputStream inputStream = null;
        try {
            //根据uri获取图片的流
            inputStream = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options的in系列的设置了，injustdecodebouond只解析图片的大小，而不加载到内存中去
            options.inJustDecodeBounds = true;
            //1.如果通过options.outHeight获取图片的宽高，就必须通过decodestream解析同options赋值
            //否则options.outheight获取不到宽高
            BitmapFactory.decodeStream(inputStream,null,options);
            //2.通过 btm.getHeight()获取图片的宽高就不需要1的解析，我这里采取第一张方式
//            Bitmap btm = BitmapFactory.decodeStream(inputStream);
            //以屏幕的宽高进行压缩
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int heightPixels = displayMetrics.heightPixels;
            int widthPixels = displayMetrics.widthPixels;
            //获取图片的宽高
            int outHeight = options.outHeight;
            int outWidth = options.outWidth;
            //heightPixels就是要压缩后的图片高度，宽度也一样
            int a = (int) Math.ceil((outHeight/(float)heightPixels));
            int b = (int) Math.ceil(outWidth/(float)widthPixels);
            //比例计算,一般是图片比较大的情况下进行压缩
            int max = Math.max(a, b);
            if(max > 1){
                options.inSampleSize = max;
            }
            //解析到内存中去
            options.inJustDecodeBounds = false;
//            根据uri重新获取流，inputstream在解析中发生改变了
            Stream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(Stream, null, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
                if(Stream != null){
                    Stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return  null;
    }
    public void createFile() {
        String path = Environment.getExternalStorageDirectory() + "/myphoto/";
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }
    }
}