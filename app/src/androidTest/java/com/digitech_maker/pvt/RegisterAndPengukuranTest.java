package com.digitech_maker.pvt;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RegisterAndPengukuranTest {

    @Rule
    public ActivityScenarioRule<RegisterActivity> registerActivityRule =
            new ActivityScenarioRule<>(RegisterActivity.class);

    @Rule
    public ActivityScenarioRule<MainWindow> mainWindowRule =
            new ActivityScenarioRule<>(MainWindow.class);

    @Before
    public void enableMockGPS() {
        // Simulasi GPS aktif
        Context context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            // Hapus penyedia mock jika sudah ada sebelumnya
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (IllegalArgumentException e) {
            // Penyedia tidak ada, abaikan
        }

        locationManager.addTestProvider(
                LocationManager.GPS_PROVIDER,
                false, // requiresNetwork
                false, // requiresSatellite
                false, // requiresCell
                false, // hasMonetaryCost
                true,  // supportsAltitude
                true,  // supportsSpeed
                true,  // supportsBearing
                0,     // powerRequirement (0=low)
                android.location.Criteria.ACCURACY_FINE // accuracy
        );

        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);


        Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLatitude(-6.200000); // Latitude dummy
        mockLocation.setLongitude(106.816666); // Longitude dummy
        mockLocation.setAccuracy(1); // Tingkat akurasi
        mockLocation.setTime(System.currentTimeMillis()); // Waktu sekarang
        mockLocation.setElapsedRealtimeNanos(System.nanoTime()); // Waktu sistem

        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation);
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

    }

    @Test
    public void testRegisterAndMeasurement() {
        // **Step 1: Registrasi**
        // Isi data registrasi
        onView(withId(R.id.namaobservant)).perform(typeText("John Doe"));
        onView(withId(R.id.password)).perform(typeText("password123"));
        onView(withId(R.id.company)).perform(typeText("TechCorp"));

        // Pilih tanggal lahir menggunakan DatePicker
        onView(withId(R.id.birthdate)).perform(click());
        onView(ViewMatchers.isAssignableFrom(android.widget.DatePicker.class))
                .perform(androidx.test.espresso.contrib.PickerActions.setDate(1990, 1, 1));
        onView(withText("OK")).perform(click());

        // Klik tombol register
        onView(withId(R.id.registerButton)).perform(click());

        // Verifikasi registrasi berhasil
        onView(withText("User Registered Successfully")).inRoot(new ToastMatcher())
                .check(matches(withText("User Registered Successfully")));

        // Logout setelah registrasi
        onView(withId(R.id.logoutButton)).perform(click());

        // **Step 2: Login**
        // Isi data login
        onView(withId(R.id.namaobservantEditText)).perform(typeText("John Doe"));
        onView(withId(R.id.passwordEditText)).perform(typeText("password123"));
        Espresso.closeSoftKeyboard();

        // Klik tombol login
        onView(withId(R.id.loginButton)).perform(click());

        // Verifikasi login berhasil
        onView(withText("Login Successful")).inRoot(new ToastMatcher())
                .check(matches(withText("Login Successful")));

        // **Step 3: Pilih Pengaturan Tes**
        // Masuk ke MainWindow dan pilih pengaturan tes
        onView(withId(R.id.radioPengukuran)).perform(typeText("30")); // Frekuensi 30
        onView(withId(R.id.radioPeriode)).perform(typeText("10")); // Delay 10 detik
        Espresso.closeSoftKeyboard();

        // Klik tombol "Start Test"
        onView(withId(R.id.cahayaBtn)).perform(click());

        // **Step 4: Verifikasi Tes Dimulai**

        // Tunggu tes selesai (simulasi waktu)
        try {
            Thread.sleep(5000); // Tunggu selama tes berlangsung
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // **Step 5: Verifikasi Hasil Tes**
        onView(withText("Done")).check(matches(ViewMatchers.isDisplayed())); // Pesan "Done"
        onView(withId(R.id.buttonnext)).check(matches(ViewMatchers.isDisplayed())); // Tombol "Next"

        // Klik tombol "Next" untuk melihat hasil
        onView(withId(R.id.buttonnext)).perform(click());


    }
}
