package vn.edu.vhu.ltdd.a1lifecycle;
 
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//NC1
import java.util.LinkedHashMap;
 
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
 
public class MainActivity extends AppCompatActivity {
 
    // TODO: thay 2201234567 bằng MSSV của bạn
    private static final String TAG = "A1_2201234567";
 
    private TextView tvLog;
    private final StringBuilder history = new StringBuilder();
    private int step = 0;

    private TextView tvCallbackCount;
    private final LinkedHashMap<String, Integer> callbackCounts = new LinkedHashMap<>();

    //Update Counter for NC1
    private void updateCallbackCount() {
        StringBuilder text = new StringBuilder("Số lần gọi callback:\n");

        for (String callback : callbackCounts.keySet()) {
            text.append(callback)
                    .append(": ")
                    .append(callbackCounts.get(callback))
                    .append('\n');
        }

        if (tvCallbackCount != null) {
            tvCallbackCount.setText(text.toString().trim());
        }
    }
 
    /** Ghi một sự kiện ra Logcat và hiển thị lên màn hình. */
    private void logEvent(String event) {
        step++;

        String time = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
                .format(new Date());

        String line = step + ". [" + time + "] " + event;

        Log.d(TAG, line);

        history.append(line).append('\n');

        if (tvLog != null) {
            tvLog.setText(history);
        }

        // Lấy tên callback từ chuỗi event
        String callbackName;
        int spaceIndex = event.indexOf(' ');

        if (spaceIndex > 0) {
            callbackName = event.substring(0, spaceIndex);
        } else {
            callbackName = event;
        }

        // Tăng số lần gọi callback
        if (callbackCounts.containsKey(callbackName)) {
            callbackCounts.put(
                    callbackName,
                    callbackCounts.get(callbackName) + 1
            );
        } else {
            callbackCounts.put(callbackName, 1);
        }

        // Cập nhật bảng đếm trên giao diện
        updateCallbackCount();
    }


 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
 
        tvLog = findViewById(R.id.tvLog);
        tvCallbackCount = findViewById(R.id.tvCallbackCount);
        Button btnClear = findViewById(R.id.btnClear);
        Button btnCrash = findViewById(R.id.btnCrash);
        Button btnFinish = findViewById(R.id.btnFinish);
 
        btnClear.setOnClickListener(v -> {
            history.setLength(0);
            step = 0;
            callbackCounts.clear();
            tvLog.setText("");
            tvCallbackCount.setText("");
            Log.i(TAG, "---- Đã xóa lịch sử ----");
        });
 
        // Nút cố ý gây lỗi để luyện đọc stack trace trong Logcat
        btnCrash.setOnClickListener(v -> {
            try {
                String ten = null;
                Log.d(TAG, "Độ dài tên: " + ten.length());
            } catch (NullPointerException e) {
                Log.e(TAG, "Bắt được lỗi NullPointerException", e);
                Toast.makeText(this, "Đã bắt lỗi: tên đang null", Toast.LENGTH_SHORT).show();
            }
        });


        // finish() để quan sát onDestroy (Android 12+: Back không hủy Activity gốc)
        btnFinish.setOnClickListener(v -> finish());
 
        String state = (savedInstanceState == null) ? "= null" : "!= null";
        logEvent("onCreate (savedInstanceState " + state + ")");
    }
 
    @Override
    protected void onStart() {
        super.onStart();
        logEvent("onStart");
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        logEvent("onResume");
    }
 
    @Override
    protected void onPause() {
        super.onPause();
        logEvent("onPause");
    }
 
    @Override
    protected void onStop() {
        super.onStop();
        logEvent("onStop");
    }
 
    @Override
    protected void onRestart() {
        super.onRestart();
        logEvent("onRestart");
    }
 
    @Override
    protected void onDestroy() {
        logEvent("onDestroy");
        super.onDestroy();
    }
 
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        logEvent("onSaveInstanceState");
    }
}
