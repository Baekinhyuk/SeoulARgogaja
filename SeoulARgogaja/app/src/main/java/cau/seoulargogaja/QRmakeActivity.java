package cau.seoulargogaja;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import java.net.MalformedURLException;
import java.util.Random;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRmakeActivity extends AppCompatActivity {

    String TAG = "GenerateQRCode";
    EditText edtValue;
    TextView code_Image;
    ImageView qrImage, btnCancel;
    Button start,generalstart;
    String inputValue;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    String readurl;
    private TextView edittitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrmake);

        edittitle = (TextView) findViewById(R.id.qr_make_title);
        edittitle.setText(edittitle.getText());

        edittitle.setPaintFlags(edittitle.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

        qrImage = (ImageView) findViewById(R.id.QR_Image);
        start = (Button) findViewById(R.id.start);
        generalstart = (Button)findViewById(R.id.generalstart);
        code_Image = (TextView)findViewById(R.id.code_Image);

        btnCancel = (ImageView) findViewById(R.id.qrmakecancel);
        // 취소 버튼 누르면 이전 화면으로 돌아감
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Phprequest phprequest = new Phprequest();
                readurl = phprequest.BASE_URL +"planlist_output.php?planlistid="+ phprequest.getResult_planlistid();
                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;
                int smallerDimension = width < height ? width : height;
                smallerDimension = smallerDimension * 3 / 4;

                qrgEncoder = new QRGEncoder(
                        readurl, null,
                        QRGContents.Type.TEXT,
                        smallerDimension);
                try {
                    bitmap = qrgEncoder.encodeAsBitmap();
                    qrImage.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    Log.v(TAG, e.toString());
                }
            }
        });

        generalstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Phprequest phprequest = new Phprequest();
                regist_wallet(phprequest.getResult_planlistid());
            }
        });

    }

    public void regist_wallet(int planlistid){
        try {
            Phprequest request = new Phprequest(Phprequest.BASE_URL + "regist_code.php");
            int minimumValue = 100000000;
            int maximumValue = 999999999;

            Random random = new Random();
            int randomValue = random.nextInt(maximumValue - minimumValue + 1) + minimumValue;

            request.regist_code(randomValue,planlistid);
            code_Image.setText(Integer.toString(randomValue));

        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }
}
