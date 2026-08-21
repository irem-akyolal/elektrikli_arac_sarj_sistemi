# ⚡ Elektrikli Araç Şarj Sistemi

Elektrikli araç kullanıcılarının herhangi bir mobil uygulama indirmeden veya üyelik oluşturmadan şarj istasyonlarını görüntüleyebilmesini, uygun connector'ı seçebilmesini, ödeme yaparak şarj işlemini başlatabilmesini ve işlem sonrasında faturasını alabilmesini sağlayan web tabanlı elektrikli araç şarj sistemidir.

Proje; **Java, Spring Boot, React, PostgreSQL, Docker, Google Maps, OCPI ve İyzico Sanbox** kullanılarak geliştirilmiştir.

> **Amaç:** Kullanıcıya mümkün olduğunca basit bir şarj deneyimi sunarken, arka planda güvenli, düzenli ve geliştirilebilir bir sistem oluşturmak.

<img width="1122" height="857" alt="image" src="https://github.com/user-attachments/assets/6cb077b4-e7fb-47d0-b85a-8f7840cf4f8c" />

##  Projenin Amacı

Elektrikli araç kullanıcılarının şarj işlemi başlatmak için farklı mobil uygulamalar indirmesi veya üyelik oluşturması gerekmemesi hedeflenmiştir.

Kullanıcı yalnızca gerekli bilgileri sağlayarak:

1. Şarj istasyonlarını görüntüleyebilir,
2. Uygun connector'ı seçebilir,
3. Şarj için provizyon tutarını belirleyebilir,
4. Ödeme işlemini gerçekleştirebilir,
5. Şarj işlemini başlatabilir,
6. Şarj sürecini takip edebilir,
7. İşlem sonunda oluşan faturayı e-posta üzerinden alabilir.

Sistem kullanıcı tarafında sade bir deneyim sunarken; ödeme, provizyon, şarj oturumu, OCPI haberleşmesi, hata yönetimi ve admin operasyonları gibi arka plan süreçlerini yönetebilecek şekilde tasarlanmıştır.

---

#  Temel Özellikler

### 👤 Kullanıcı

* Üyelik gerektirmeyen kullanım
* Mobil öncelikli web arayüzü
* Google Maps üzerinden istasyon görüntüleme
* Harita ve liste görünümü
* İstasyon detaylarını görüntüleme
* EVSE ve connector bilgilerini görüntüleme
* Connector durumunu görüntüleme
* Güncel birim fiyatların görüntülenmesi
* Connector seçimi
* Araç plakası bilgisi
* Fatura e-posta adresi
* İyzico üzerinden ödeme
* Ön provizyon oluşturma
* Şarj işlemi başlatma
* Şarj oturumu takibi
* Tüketim takibi
* Ödeme capture işlemi
* İade işlemleri
* Fatura oluşturma
* Faturanın e-posta ile gönderilmesi

### 🛡️ Admin

* Dashboard
* İstasyon yönetimi
* EVSE / Connector yönetimi
* Şarj işlemlerinin takibi
* Ödeme işlemlerinin yönetimi
* Provizyon işlemlerinin takibi
* Kullanıcı hareketleri
* E-posta geçmişi
* Sistem parametreleri

---

# 🗺️ İstasyon Keşfi

Kullanıcılar şarj istasyonlarını **harita** veya **liste** görünümü üzerinden görüntüleyebilir.

Harita tarafında **Google Maps API** kullanılarak istasyonların konumları gösterilmektedir.

Harita üzerinde kullanıcı deneyimini kolaylaştırmak amacıyla çeşitli özellikler geliştirilmiştir:

- Şarj istasyonlarının harita üzerinde marker'lar ile gösterilmesi
- Marker'a tıklanarak ilgili istasyonun detaylarının görüntülenmesi
- Kullanıcının mevcut konumunun alınması
- Haritanın kullanıcının mevcut konumuna otomatik olarak odaklanması
- Kullanıcının konumuna göre yakınındaki istasyonların gösterilmesi
- Harita ve liste görünümü arasında geçiş yapılabilmesi

Bir istasyon seçildiğinde istasyona ait:

- EVSE'ler
- Connector'lar
- Connector durumları
- Birim fiyatlar

görüntülenebilir.

Bu yapı sayesinde kullanıcı, bulunduğu konuma göre uygun şarj istasyonlarını hızlı bir şekilde keşfedebilir ve seçtiği istasyonun detaylarına harita üzerinden doğrudan ulaşabilir.

<img width="1250" height="674" alt="image" src="https://github.com/user-attachments/assets/a730b4b8-d412-4ba1-8492-121cad91d275" />



<img width="1243" height="708" alt="image" src="https://github.com/user-attachments/assets/2783570d-62a4-4012-99fd-22f49004c916" />



---

# ⚡ Şarj İşlemi

Şarj işlemi, kullanıcının uygun bir connector seçmesiyle başlar.

Temel kullanıcı akışı:

```text
İstasyon Seçimi
       ↓
Connector Seçimi
       ↓
Araç Bilgileri
       ↓
E-posta Bilgisi
       ↓
Provizyon Seçimi
       ↓
Ödeme
       ↓
Ön Provizyon
       ↓
Şarj Başlatma
       ↓
Şarj Oturumu
       ↓
Tüketim Takibi
       ↓
Şarjın Tamamlanması
       ↓
Capture / Refund
       ↓
Fatura
       ↓
E-posta
```

<img width="1293" height="649" alt="image" src="https://github.com/user-attachments/assets/e29c6cac-4299-46f3-9d0b-b36d6840e2c9" />


<img width="1186" height="661" alt="image" src="https://github.com/user-attachments/assets/5df8a4f0-ca64-47b3-a024-95caae960047" />



# 💳 Ödeme ve Provizyon

Ödeme işlemleri, geliştirme ve test amacıyla **İyzico Sandbox** ortamı kullanılarak gerçekleştirilmiştir. Gerçek finansal işlemler gerçekleştirilmemektedir.

Şarj başlamadan önce kullanıcı için belirlenen enerji miktarlarına göre provizyon seçenekleri oluşturulur.

Örneğin:

|  Enerji |        Provizyon |
| ------: | ---------------: |
|  50 kWh | Hesaplanan tutar |
|  70 kWh | Hesaplanan tutar |
| 100 kWh | Hesaplanan tutar |
| 120 kWh | Hesaplanan tutar |

Provizyon tutarı, seçilen connector'ın güncel enerji fiyatı üzerinden hesaplanır.

Ödeme başarılı olduğunda seçilen tutar için ön provizyon oluşturulur.

Şarj işlemi tamamlandığında ise gerçek tüketim üzerinden nihai ödeme tutarı hesaplanır.

```text
              Ödeme
                │
                ▼
          Ön Provizyon
                │
                ▼
         Şarj İşlemi
                │
                ▼
        Gerçek Tüketim
                │
          ┌─────┴─────┐
          ▼           ▼
       Capture      Refund
```

Bu yapı sayesinde şarj başlamadan önce belirlenen tutar kadar güvenli bir ödeme limiti oluşturulur ve şarj tamamlandığında gerçek tüketim üzerinden işlem sonuçlandırılır.

<img width="1212" height="660" alt="image" src="https://github.com/user-attachments/assets/ef50883e-7844-4c70-9fb7-6c6567069c52" />



<img width="1280" height="653" alt="image" src="https://github.com/user-attachments/assets/44eb769b-c87c-4ed2-9743-6662db9b7e5b" />



---

# 🔌 OCPI Entegrasyonu

Projenin önemli bileşenlerinden biri **OCPI (Open Charge Point Interface)** entegrasyonudur.

OCPI, elektrikli araç şarj ağı bileşenlerinin farklı sistemler arasında standart bir yapı üzerinden haberleşmesini sağlayan bir protokoldür.

Projede OCPI yaklaşımı şarj istasyonları ve şarj operasyonlarıyla ilgili süreçlerde kullanılmıştır.

Sistem içerisinde özellikle:

* Location
* EVSE
* Connector
* Connector Status
* Charging Session
* Remote Start

gibi bileşenler ve işlemler ele alınmıştır.

Genel iletişim yapısı:

```text
User
 │
 ▼
Frontend
 │
 ▼
Backend
 │
 ▼
OCPI Integration
 │
 ▼
Charging Network / CPO
```

Backend'in OCPI entegrasyonunu yönetmesi sayesinde frontend'in doğrudan şarj ağıyla haberleşmesi yerine işlemler merkezi bir backend üzerinden kontrol edilir.

### OCPI Kaynakları

* [OCPI GitHub Repository](https://github.com/ocpi/ocpi)
* [OCPI 2.2.1 Specification](https://evroaming.org/wp-content/uploads/2024/11/OCPI-2.2.1-d2.pdf)

---

# 🧾 Şarj Sonrası İşlemler

Şarj işlemi tamamlandığında sistem gerçek tüketim miktarını hesaplar.

Ardından:

1. Şarj oturumu tamamlanır.
2. Gerçek tüketim hesaplanır.
3. Ödeme işlemi sonuçlandırılır.
4. Kullanılmayan provizyon tutarı varsa iade edilir.
5. Fatura oluşturulur.
6. Fatura kullanıcının e-posta adresine gönderilir.

Bu sayede ödeme ile gerçek şarj tüketimi arasındaki fark sistem tarafından yönetilir.

<img width="862" height="658" alt="image" src="https://github.com/user-attachments/assets/1d0b4e4c-47b2-479e-ae90-00e2ce7556c7" />



<img width="730" height="611" alt="image" src="https://github.com/user-attachments/assets/2afe120c-a150-43ab-940f-465dc950de1f" />




<img width="1063" height="674" alt="image" src="https://github.com/user-attachments/assets/63024a3b-07ab-4c24-b397-3651096fdbee" /> 





---

# 🛡️ Admin Panel

Admin paneli, sistemdeki istasyonların ve şarj operasyonlarının yönetilmesi amacıyla geliştirilmiştir.

Admin tarafından:

* İstasyonlar
* EVSE ve connector'lar
* Şarj işlemleri
* Ödemeler
* Provizyonlar
* Kullanıcı hareketleri
* E-posta kayıtları
* Sistem parametreleri

takip edilebilir ve yönetilebilir.

Dashboard üzerinde sistem kullanımına ilişkin temel bilgiler özet olarak sunulur.

<img width="1878" height="865" alt="image" src="https://github.com/user-attachments/assets/8c3ed107-9142-4ae5-a333-8c4e2af11a85" />



<img width="1876" height="674" alt="image" src="https://github.com/user-attachments/assets/918f22cd-c278-4ede-9d25-712f45cbb092" />





### 🔐 Rol Bazlı Yetkilendirme

Admin panelinde farklı kullanıcıların sistem üzerindeki erişim seviyelerini kontrol etmek amacıyla **rol bazlı yetkilendirme (RBAC)** uygulanmıştır.

Sistemde üç farklı admin rolü bulunmaktadır:

| Rol | Açıklama |
|---|---|
| **Super Admin** | Sistemin tüm yönetim ve operasyon ekranlarına erişebilir. |
| **Operator** | Operasyonel işlemler ve ilgili yönetim ekranlarına erişebilir. |
| **Viewer** | Sistemdeki belirli bilgileri görüntüleyebilir, yönetimsel işlemlere erişemez. |

Rollere göre kullanıcıların erişebileceği sayfalar ve gerçekleştirebileceği işlemler sınırlandırılmıştır. Kullanıcının rolüne yetkisi olmayan yönetim ekranları arayüzde gösterilmemekte ve backend tarafında da yetki kontrolleri uygulanmaktadır.



---

#  Mimari

Backend tarafında katmanlı ve sorumlulukların ayrıştırılmasına dayalı bir mimari yapı kullanılmıştır.

Proje içerisinde controller, service ve repository katmanlarının yanında farklı sorumluluklar için ayrı modüller ve yapılar oluşturulmuştur.


```text
Backend
│
├── Controller
├── Service
├── Repository
├── Entity
│
├── DTO
├── Mapper
├── Specification
│
├── Security
├── Filter
├── Config
├── Exception
│
├── Payment
├── Email
├── OCPI
└── Scheduler
```

Ödeme ve OCPI gibi dış servis entegrasyonlarının business logic'ten mümkün olduğunca ayrıştırılmasına dikkat edilmiştir.

Bu sayede ileride farklı ödeme sağlayıcılarının veya farklı OCPI sağlayıcılarının sisteme eklenebilmesi hedeflenmiştir.



# ⚙️ Backend

Backend **Java ve Spring Boot** kullanılarak geliştirilmiştir.

Kullanılan başlıca yapılar:

* Spring Boot
* Spring Data JPA
* Hibernate
* Spring Security
* REST API
* DTO
* Mapper
* Validation
* Global Exception Handling
* Transaction Management
* Pagination
* Filtering
* Sorting
* Scheduler
* Async Task
* Retry Mechanisms
* Rate Limiting
* Audit Log
* Soft Delete
* Versioning
* Configuration Management
* Email Service
* Payment Service
* OCPI Client
* Queue / Event yapısı


# 🖥️ Frontend

Frontend tarafında **React + Vite** kullanılmıştır.

Arayüz özellikle mobil kullanıcı deneyimi dikkate alınarak tasarlanmıştır.

Temel frontend bölümleri:

* Ana sayfa
* İstasyon haritası
* İstasyon listesi
* İstasyon detayları
* Connector seçimi
* Şarj başlatma
* Ödeme
* Şarj durumu
* Admin Dashboard
* Admin yönetim ekranları



# 🗄️ Veritabanı

Veritabanı olarak **PostgreSQL** kullanılmıştır.

Sistemde temel olarak:

* User
* Admin
* Location
* EVSE
* Connector
* Charging Session
* Payment
* Provision
* Invoice

gibi domain modelleri bulunmaktadır.

Bu modeller arasındaki ilişkiler istasyon, connector, şarj oturumu ve ödeme süreçlerini destekleyecek şekilde oluşturulmuştur.






# 🐳 Docker

Projede **PostgreSQL veritabanının geliştirme ortamında container içerisinde çalıştırılması** için Docker kullanılmıştır.

Backend ve frontend Docker container'ları içerisinde çalıştırılmamaktadır.

Projenin çalışma yapısı:


```text
┌───────────────────────┐
│    React Frontend     │
│      Local / Vite     │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────┐
│   Spring Boot Backend │
│         Local         │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────┐
│      PostgreSQL       │
│    Docker Container   │
└───────────────────────┘
```

# 🛠️ Teknolojiler

| Alan              | Teknolojiler           |
| ----------------- | ---------------------- |
| Backend           | Java, Spring Boot      |
| Database          | PostgreSQL             |
| ORM               | JPA / Hibernate        |
| Frontend          | React, Vite            |
| Authentication    | Spring Security        |
| Payment           | İyzico                 |
| Charging Protocol | OCPI                   |
| Maps              | Google Maps API        |
| Database Container| Docker, Docker Compose|
| Version Control   | Git, GitHub            |

---

# 🚀 Kurulum
Gereksinimler

Projeyi çalıştırabilmek için aşağıdaki araçların sistemde kurulu olması gerekmektedir:

Java 21+
Node.js
npm
Docker Desktop
Git
Repository

Projeyi GitHub üzerinden klonlayın:

git clone https://github.com/irem-akyolal/elektrikli_arac_sarj_sistemi.git
cd elektrikli_arac_sarj_sistemi


# 🐳 PostgreSQL'i Docker ile Başlatma

Projede PostgreSQL veritabanı Docker container içerisinde çalıştırılmaktadır.

Repository'nin ana dizininde aşağıdaki komutu çalıştırın:

docker compose up -d

Çalışan container'ları kontrol etmek için:

docker ps

PostgreSQL container'ı çalışır durumda olduktan sonra backend ve frontend uygulamaları local ortamda başlatılabilir.

Backend ve frontend Docker container içerisinde çalıştırılmamaktadır. Docker yalnızca PostgreSQL veritabanının geliştirme ortamında çalıştırılması için kullanılmaktadır.

# ⚙️ Backend

Spring Boot uygulamasını çalıştırmak için repository'nin ana dizininde:

Windows
mvnw.cmd spring-boot:run
Linux / macOS
./mvnw spring-boot:run

Backend başlatılmadan önce gerekli configuration değerlerinin tanımlanmış olması gerekmektedir.

# 🖥️ Frontend

Frontend dizinine geçin:

cd frontend

Gerekli paketleri yükleyin:

npm install

Geliştirme sunucusunu başlatın:

npm run dev

Frontend uygulaması çalışmaya başladıktan sonra terminalde belirtilen local adres üzerinden uygulamaya erişilebilir.

API key, database password, payment secret ve benzeri hassas bilgiler GitHub repository'sine yüklenmemelidir.

# 🔐 Configuration

Uygulamanın çalışması için gerekli hassas configuration değerleri local configuration veya environment variable üzerinden sağlanmalıdır.

Örnek configuration:

DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=


IYZICO_API_KEY=
IYZICO_SECRET_KEY=


GOOGLE_MAPS_API_KEY=


MAIL_USERNAME=
MAIL_PASSWORD=

Bu değerler kullanılan Spring Boot configuration yapısına göre application.properties, application.yml veya environment variable üzerinden tanımlanabilir.




# 📚 Kaynaklar

* [OCPI GitHub](https://github.com/ocpi/ocpi)
* [OCPI 2.2.1 Specification](https://evroaming.org/wp-content/uploads/2024/11/OCPI-2.2.1-d2.pdf)
* [İyzico Developer Documentation](https://docs.iyzico.com/)
* [Google Maps Platform Documentation](https://developers.google.com/maps)
* [Google Maps JavaScript API Documentation](https://developers.google.com/maps/documentation/javascript)

---

## 🎓 Proje Hakkında

Bu proje, yazılım mühendisliği stajı kapsamında geliştirilmiştir.

Proje sürecinde backend, frontend, veritabanı, ödeme sistemi, OCPI entegrasyonu, Docker altyapısı ve admin yönetim paneli gibi farklı bileşenler üzerinde çalışılmıştır.

Projenin temel amacı yalnızca çalışan bir uygulama geliştirmek değil; gerçek bir elektrikli araç şarj sisteminde karşılaşılabilecek kullanıcı, ödeme, şarj ve yönetim süreçlerini deneyimleyerek **geliştirilebilir ve sürdürülebilir bir yazılım yapısı** oluşturmaktır.
