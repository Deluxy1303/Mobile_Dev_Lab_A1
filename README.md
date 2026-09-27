# README – Lab A1: Hoàn thành NC1 và NC2

## 1. Mục đích

README này mô tả những thay đổi đã thực hiện trong project Android A1 để hoàn thành 2 bài nâng cao ở Phần 7:

- **NC1:** Đếm số lần gọi của từng callback vòng đời Activity và hiển thị bảng đếm phía trên vùng log.
- **NC2:** Sửa nút **Gây lỗi** để ứng dụng không còn bị crash khi xảy ra `NullPointerException`, đồng thời hiển thị thông báo Toast.

---

## 2. Các file đã thay đổi

### File 1: `app/src/main/res/layout/activity_main.xml`

Dùng để bổ sung giao diện hiển thị số lần gọi callback cho NC1.

### File 2: `app/src/main/java/.../MainActivity.java`

Dùng để:
- Thực hiện cơ chế đếm callback cho **NC1**.
- Sửa xử lý lỗi `NullPointerException` cho **NC2**.

### Các file không cần thay đổi

- `AndroidManifest.xml`
- `strings.xml`
- Các file Gradle
- Các Activity khác

Không có callback vòng đời nào bị xóa.

---

# 3. NC1 – Đếm số lần gọi mỗi callback

## 3.1. Thay đổi trong `activity_main.xml`

Trong file:

```text
app/src/main/res/layout/activity_main.xml
```

đã thêm một `TextView` nằm **sau hàng 3 nút và trước `ScrollView` chứa log**:

```xml
<TextView
    android:id="@+id/tvCallbackCount"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="12dp"
    android:text="Số lần gọi callback:"
    android:textSize="14sp" />
```

`TextView` này dùng để hiển thị ví dụ:

```text
Số lần gọi callback:
onCreate: 1
onStart: 1
onResume: 1
```

---

## 3.2. Thay đổi trong `MainActivity.java`

### Thêm `LinkedHashMap`

Thêm import:

```java
import java.util.LinkedHashMap;
```

Dùng `LinkedHashMap<String, Integer>` để lưu tên callback và số lần callback được gọi.

### Thêm biến đếm

```java
private TextView tvCallbackCount;

private final LinkedHashMap<String, Integer> callbackCounts
        = new LinkedHashMap<>();
```

Trong đó:

- `tvCallbackCount`: tham chiếu đến `TextView` trên giao diện.
- `callbackCounts`: lưu số lần gọi của từng callback.

Ví dụ:

```text
onCreate  -> 1
onStart   -> 2
onResume  -> 2
onPause   -> 1
```

---

## 3.3. Thêm hàm `updateCallbackCount()`

Đã thêm:

```java
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
```

Hàm này duyệt bộ đếm và cập nhật nội dung `tvCallbackCount`.

---

## 3.4. Sửa `logEvent()`

Code ban đầu của `logEvent()` chỉ ghi log và hiển thị lịch sử.

Đã bổ sung vào `logEvent()` các bước:

1. Lấy tên callback từ chuỗi `event`.
2. Kiểm tra callback đã tồn tại trong `callbackCounts` chưa.
3. Nếu có thì tăng số đếm.
4. Nếu chưa có thì tạo giá trị `1`.
5. Gọi `updateCallbackCount()`.

Phần xử lý chính:

```java
String callbackName;
int spaceIndex = event.indexOf(' ');

if (spaceIndex > 0) {
    callbackName = event.substring(0, spaceIndex);
} else {
    callbackName = event;
}

if (callbackCounts.containsKey(callbackName)) {
    callbackCounts.put(
            callbackName,
            callbackCounts.get(callbackName) + 1
    );
} else {
    callbackCounts.put(callbackName, 1);
}

updateCallbackCount();
```

Các callback vòng đời đều gọi `logEvent()`, nên chỉ cần sửa `logEvent()` thì mọi callback được đếm tự động.

---

## 3.5. Kết nối `TextView` trong `onCreate()`

Trong `onCreate()` đã thêm:

```java
tvCallbackCount = findViewById(R.id.tvCallbackCount);
```

Dòng này liên kết biến Java với `TextView` đã thêm trong XML.

---

## 3.6. Sửa nút `Xóa log`

Đã bổ sung xóa bộ đếm:

```java
callbackCounts.clear();
tvCallbackCount.setText("");
```

Đoạn xử lý:

```java
btnClear.setOnClickListener(v -> {
    history.setLength(0);
    step = 0;

    callbackCounts.clear();

    tvLog.setText("");
    tvCallbackCount.setText("");

    Log.i(TAG, "---- Đã xóa lịch sử ----");
});
```

Mục đích là khi bắt đầu một kịch bản mới, cả log và số lần callback đều được đưa về trạng thái ban đầu.

---

## 3.7. Các callback vòng đời không bị xóa

Giữ nguyên:

```text
onCreate()
onStart()
onResume()
onPause()
onStop()
onRestart()
onDestroy()
onSaveInstanceState()
```

Không cần viết lại phần tăng bộ đếm riêng cho từng callback vì tất cả đã gọi `logEvent()`.

---

# 4. NC2 – Sửa nút Gây lỗi

## 4.1. Lỗi ban đầu

Code ban đầu của nút `Gây lỗi`:

```java
btnCrash.setOnClickListener(v -> {
    String ten = null;
    Log.d(TAG, "Độ dài tên: " + ten.length());
});
```

`ten` có giá trị `null` nhưng vẫn gọi `ten.length()`, vì vậy phát sinh:

```text
java.lang.NullPointerException
```

và ứng dụng bị crash.

---

## 4.2. Cách sửa

Đã thay phần xử lý của `btnCrash` bằng:

```java
btnCrash.setOnClickListener(v -> {
    try {
        String ten = null;
        Log.d(TAG, "Độ dài tên: " + ten.length());
    } catch (NullPointerException e) {
        Log.e(TAG, "Bắt được lỗi NullPointerException", e);
        Toast.makeText(
                this,
                "Đã bắt lỗi: tên đang null",
                Toast.LENGTH_SHORT
        ).show();
    }
});
```

### Những thay đổi cụ thể

**Giữ lại:**

```java
String ten = null;
```

để vẫn tạo ra tình huống lỗi phục vụ bài thực hành.

**Thêm `try`:**

Đưa đoạn có khả năng phát sinh lỗi vào khối `try`.

**Thêm `catch`:**

```java
catch (NullPointerException e)
```

để bắt lỗi và ngăn ứng dụng crash.

**Thêm `Log.e()`:**

```java
Log.e(TAG, "Bắt được lỗi NullPointerException", e);
```

để ghi lỗi ở mức Error và giữ stack trace trong Logcat.

**Thêm Toast:**

```java
Toast.makeText(
        this,
        "Đã bắt lỗi: tên đang null",
        Toast.LENGTH_SHORT
).show();
```

để thông báo trực tiếp cho người dùng.

---

# 5. Kiểm tra NC1

Sau khi chạy app:

### Mở app lần đầu

Hiển thị:

```text
Số lần gọi callback:
onCreate: 1
onStart: 1
onResume: 1
```

### Nhấn Home

Các callback mới được cộng vào bộ đếm.

### Mở lại từ Recents

`onRestart`, `onStart`, `onResume` tiếp tục được cộng.

### Xoay màn hình

Các callback phát sinh do Activity bị tạo lại sẽ được đếm.

### Nhấn Xóa log

Log và bảng đếm đều được xóa để thực hiện kịch bản tiếp theo.

---

# 6. Kiểm tra NC2

Nhấn **Gây lỗi**.

### Trước khi sửa

App bị crash do `NullPointerException`.

### Sau khi sửa

- App không bị đóng.
- Logcat có `Log.e()` báo `NullPointerException`.
- Màn hình hiển thị Toast:

```text
Đã bắt lỗi: tên đang null
```

Như vậy NC2 đã được hoàn thành.

---

# 7. Tổng kết các thay đổi

| File | Thay đổi | Mục đích |
|---|---|---|
| `activity_main.xml` | Thêm `tvCallbackCount` | Hiển thị số lần callback |
| `MainActivity.java` | Thêm `LinkedHashMap` | Lưu số lần callback |
| `MainActivity.java` | Thêm `updateCallbackCount()` | Cập nhật bảng đếm |
| `MainActivity.java` | Sửa `logEvent()` | Tăng bộ đếm sau mỗi callback |
| `MainActivity.java` | Sửa `onCreate()` | Kết nối `tvCallbackCount` |
| `MainActivity.java` | Sửa `btnClear` | Xóa bộ đếm khi bắt đầu lại |
| `MainActivity.java` | Sửa `btnCrash` | Bắt `NullPointerException` và Toast |
| `AndroidManifest.xml` | Không sửa | Không liên quan NC1/NC2 |
| `strings.xml` | Không sửa | Không bắt buộc cho NC1/NC2 |
| Các callback vòng đời | Không xóa | Giữ nguyên chức năng Lab A1 |

---

# 8. Kết quả cuối cùng

**NC1:** Ứng dụng tự động đếm và hiển thị số lần gọi của từng callback vòng đời Activity.

**NC2:** Nút `Gây lỗi` không còn làm ứng dụng crash; lỗi `NullPointerException` được bắt, ghi vào Logcat và thông báo bằng Toast.
