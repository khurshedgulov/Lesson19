package company.my.lesson19;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    // Объявить переменные для хранения ссылок к виджетам макета
    Button photoCamera;
    ImageView image;
    Button videoCamera;
    // VideoView это новый виджет который не проходили, но который служит
    // для воспроизведения видео, также поддерживается создание кнопок контроля
    VideoView video;

    // Коды используемые для отправки запроса на получение разрешений
    // и получения результата снятых на камеру данных
    // RCODE для фото, VCODE для видео
    public static final int RCODE = 0;
    public static final int VCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoCamera =findViewById(R.id.openCamera);
        image = findViewById(R.id.image);

        // Установить обработчик события клик на кнопке снятия фото
        photoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверить разрешение для доступа к камере
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    // Если не дано разрешение на использование камеры, спросить разрешение и отправить код запроса
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                            Manifest.permission.CAMERA}, RCODE);

                } else {
                    // Если получено разрешение открыть камеру для снятия фото
                    showCamera();
                }
            }
        });

        // Привязать кнопку открытия камеры для снятия видео к кнопке
        videoCamera = findViewById(R.id.openVideo);
        // Привязать виджет VideoView к переменной
        video = findViewById(R.id.video);
        // Класс MediaController служит для показа кнопок управления воспроизведением видео в интерфейсе
        MediaController mc = new MediaController(this);
        mc.setAnchorView(video);
        // Подключить MediaController к VideoView
        video.setMediaController(mc);

        videoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Спросить разрешение на использование камеры
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    // Если нет разрешение, запросить и отправить код запроса видео
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                            Manifest.permission.CAMERA}, VCODE);

                } else {
                    // Если есть разрешение открыть камеру для снятия видео
                    openVideo();
                }
            }
        });
    }

    void showCamera() {
        // Инициализировать экземпляр камеры для снятия фото
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Запустить Activity и ждать до получения результата (пока Activity камеры не будет закрыто)
        // и отправить код запроса в Activity
        startActivityForResult(cameraIntent, RCODE);
    }

    void openVideo() {
        // Инициализация экземпляра камеры для снятия видео
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Запустить Activity и передать код запроса видео
        startActivityForResult(videoIntent, VCODE);
    }


    // Функция вызывается при возвращении значений с других Activity,
    // которые были запущены через функцию startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Если возвращенный код равен коду запроса на снятие фото и код результата имеет статус RESULT_OK
        if(requestCode == RCODE  && resultCode == RESULT_OK)
        {
            // Инициализировать переменную класса Bitmap используя полученные с камеры данные
            // в данном случае показывается миниатюра снятой фотографии
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // Указать полученное снятое фото в качестве источника ImageView
            image.setImageBitmap(photo);
        }
        // Если возвращенный код равен коду запроса на снятие видео и код результата имеет статус RESULT_OK
        if (requestCode == VCODE && resultCode == RESULT_OK)
        {
            // Инициализировать объект Uri (путь к видеофайлу) из полученных данных
            Uri videoUri = data.getData();
            // Установить в качестве источника VideoView полученный Uri (путь к видеофайлу)
            video.setVideoURI(videoUri);
            // Запустить воспроизведение снятого видео
            video.start();
        }
    }

    // Функция вызывается после запроса на разрешение использования системных ресурсов
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        // Если код запроса равен коду запроса на снятие фото или видео
        if(requestCode == RCODE || requestCode == VCODE)
        {
            // Если в списке результатов ответа на запрос разрешения есть элементы, значит разрешение дано
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Если код полученного разрешения равен коду запроса открытия камеры на съёмку фото
                // открыть камеру в режиме записи видео
                if (requestCode == RCODE) {
                    showCamera();
                }
                // Если код полученного разрешения равен коду запроса открытия камеры на съёмку видео
                // открыть камеру в режиме записи видео
                if(requestCode == VCODE) {
                    openVideo();
                }
            }
        }
    }
}