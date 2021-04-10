package pl.pecet.rx_qr_code_reader.internal.data;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.mlkit.vision.barcode.Barcode;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QrCode {

    @Getter
    private final Barcode qrCode;
    @Getter
    private final Bitmap image;

    public QrCodeType getType() {
        return QrCodeType.fromType(qrCode.getValueType());
    }

    //<editor-fold desc="delegate">
    public String getRawValue() {
        return qrCode.getRawValue();
    }

    public String getDisplayValue() {
        return qrCode.getDisplayValue();
    }

    public QrCodeEmail getEmail() {
        return new QrCodeEmail(qrCode.getEmail());
    }

    public QrCodePhone getPhone() {
        return new QrCodePhone(qrCode.getPhone());
    }

    public QrCodeSms getSms() {
        return new QrCodeSms(qrCode.getSms());
    }

    public QrCodeWiFi getWifi() {
        return new QrCodeWiFi(qrCode.getWifi());
    }

    public QrCodeUrlBookmark getUrl() {
        return new QrCodeUrlBookmark(qrCode.getUrl());
    }

    public QrCodeGeoPoint getGeoPoint() {
        return new QrCodeGeoPoint(qrCode.getGeoPoint());
    }

    public QrCodeDriverLicense getDriverLicense() {
        return new QrCodeDriverLicense(qrCode.getDriverLicense());
    }

    public QrCodeCalendarEvent getCalendarEvent() {
        return new QrCodeCalendarEvent(qrCode.getCalendarEvent());
    }

    public QrCodeContactInfo getContactInfo() {
        return new QrCodeContactInfo(qrCode.getContactInfo());
    }
    //</editor-fold>

    //<editor-fold desc="sub type class">
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QrCodeEmail {
        @Getter
        private final Barcode.Email qrCode;

        public EmailType getType() {
            return EmailType.fromType(qrCode.getType());
        }

        //<editor-fold desc="delegate">
        public String getAddress() {
            return qrCode.getAddress();
        }

        public String getSubject() {
            return qrCode.getSubject();
        }

        public String getBody() {
            return qrCode.getBody();
        }
        //</editor-fold>
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QrCodePhone {
        @Getter
        private final Barcode.Phone qrCode;

        public PhoneType getType() {
            return PhoneType.fromType(qrCode.getType());
        }

        //<editor-fold desc="delegate">
        public String getNumber() {
            return qrCode.getNumber();
        }
        //</editor-fold>
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QrCodeSms {
        @Getter
        private final Barcode.Sms qrCode;

        //<editor-fold desc="delegate">
        public String getMessage() {
            return qrCode.getMessage();
        }

        public String getPhoneNumber() {
            return qrCode.getPhoneNumber();
        }
        //</editor-fold>
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QrCodeWiFi {
        @Getter
        private final Barcode.WiFi qrCode;

        public WiFiEncryptionType getEncryptionType() {
            return WiFiEncryptionType.fromType(qrCode.getEncryptionType());
        }

        //<editor-fold desc="delegate">
        public String getSsid() {
            return qrCode.getSsid();
        }

        public String getPassword() {
            return qrCode.getPassword();
        }
        //</editor-fold>
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QrCodeUrlBookmark {
        @Getter
        private final Barcode.UrlBookmark qrCode;

        //<editor-fold desc="delegate">
        public String getTitle() {
            return qrCode.getTitle();
        }

        public String getUrl() {
            return qrCode.getUrl();
        }
        //</editor-fold>
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QrCodeGeoPoint {
        @Getter
        private final Barcode.GeoPoint qrCode;

        //<editor-fold desc="delegate">
        public double getLat() {
            return qrCode.getLat();
        }

        public double getLng() {
            return qrCode.getLng();
        }
        //</editor-fold>
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QrCodeDriverLicense {
        @Getter
        private final Barcode.DriverLicense qrCode;

        //<editor-fold desc="delegate">
        public String getDocumentType() {
            return qrCode.getDocumentType();
        }

        public String getFirstName() {
            return qrCode.getFirstName();
        }

        public String getMiddleName() {
            return qrCode.getMiddleName();
        }

        public String getLastName() {
            return qrCode.getLastName();
        }

        public String getGender() {
            return qrCode.getGender();
        }

        public String getAddressStreet() {
            return qrCode.getAddressStreet();
        }

        public String getAddressCity() {
            return qrCode.getAddressCity();
        }

        public String getAddressState() {
            return qrCode.getAddressState();
        }

        public String getAddressZip() {
            return qrCode.getAddressZip();
        }

        public String getLicenseNumber() {
            return qrCode.getLicenseNumber();
        }

        public String getIssueDate() {
            return qrCode.getIssueDate();
        }

        public String getExpiryDate() {
            return qrCode.getExpiryDate();
        }

        public String getBirthDate() {
            return qrCode.getBirthDate();
        }

        public String getIssuingCountry() {
            return qrCode.getIssuingCountry();
        }
        //</editor-fold>
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QrCodeCalendarEvent {
        @Getter
        private final Barcode.CalendarEvent qrCode;

        //<editor-fold desc="delegate">
        public String getSummary() {
            return qrCode.getSummary();
        }

        public String getDescription() {
            return qrCode.getDescription();
        }

        public String getLocation() {
            return qrCode.getLocation();
        }

        public String getOrganizer() {
            return qrCode.getOrganizer();
        }

        public String getStatus() {
            return qrCode.getStatus();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public LocalDateTime getStartLocalDateTime() {
            return localDateTimeFrom(qrCode.getStart());
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public LocalDateTime getEndLocalDateTime() {
            return localDateTimeFrom(qrCode.getEnd());
        }

        public Date getStartDate() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return localDateTimeToDate(getStartLocalDateTime());
            } else {
                return dateFrom(qrCode.getStart());
            }
        }

        public Date getEndDate() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return localDateTimeToDate(getEndLocalDateTime());
            } else {
                return dateFrom(qrCode.getEnd());
            }
        }
        //</editor-fold>

        @RequiresApi(api = Build.VERSION_CODES.O)
        private static LocalDateTime localDateTimeFrom(Barcode.CalendarDateTime value) {
            return value == null
                    ? null
                    : LocalDateTime.of(value.getYear(), value.getMonth(), value.getDay(),
                    value.getHours(), value.getMinutes(), value.getSeconds());
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private static Date localDateTimeToDate(LocalDateTime date) {
            return date == null
                    ? null
                    : new Date(date.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli());
        }

        private static Date dateFrom(Barcode.CalendarDateTime value) {
            if (value == null) {
                return null;
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.set(value.getYear(), value.getMonth(), value.getDay(), value.getHours(), value.getMinutes(), value.getSeconds());
                return calendar.getTime();
            }
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QrCodeAddress {
        @Getter
        private final Barcode.Address qrCode;

        public AddressType getType() {
            return AddressType.fromType(qrCode.getType());
        }

        //<editor-fold desc="delegate">
        @NonNull
        public String[] getAddressLines() {
            return qrCode.getAddressLines();
        }
        //</editor-fold>
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QrCodeContactInfo {
        @Getter
        private final Barcode.ContactInfo qrCode;

        //<editor-fold desc="delegate">
        public Barcode.PersonName getName() {
            return qrCode.getName();
        }

        public String getOrganization() {
            return qrCode.getOrganization();
        }

        public String getTitle() {
            return qrCode.getTitle();
        }

        @NonNull
        public List<QrCodePhone> getPhones() {
            return qrCode.getPhones().stream().map(QrCodePhone::new).collect(Collectors.toList());
        }

        @NonNull
        public List<QrCodeEmail> getEmails() {
            return qrCode.getEmails().stream().map(QrCodeEmail::new).collect(Collectors.toList());
        }

        @NonNull
        public List<String> getUrls() {
            return qrCode.getUrls();
        }

        @NonNull
        public List<QrCodeAddress> getAddresses() {
            return qrCode.getAddresses().stream().map(QrCodeAddress::new).collect(Collectors.toList());
        }

        //</editor-fold>
    }
    //</editor-fold>
}
