# Fordulatszámkalkulátor CL

Androidos fordulatszám-, kerületisebesség- és átmérő-kalkulátor a [Csiszolótárcsa WebShop](https://www.csiszolotarcsawebshop.hu/) számára. Natív Kotlin + Jetpack Compose, internet nélkül is működik.

## Funkciók

- **Három kalkulátor** alsó navigációval: fordulatszám (1/min), kerületi sebesség (m/s), átmérő (mm).
- **Szerszámtípus-választó** (vágókorong, tisztítókorong, lamellás tárcsa, fíbertárcsa, kerámia köszörűkorong, drótkefe) a típus megengedett legnagyobb kerületi sebességével.
- **Biztonsági figyelmeztetés**, ha az érték meghaladja a típus maximumát; a kerületi sebességnél a megengedett max. fordulatszámot is kiírja.
- **Gyors átmérőgombok:** 115, 125, 150, 180, 230 mm.
- **Ajánlott értékek** táblázat szerszámtípusonként.
- **Termékajánló link:** a webshop keresőjét nyitja meg az átmérővel és a szerszámtípussal (pl. „125 vágókorong”).
- **Fejléc:** logó (a webshopra visz), hívás gomb (tárcsázó), email gomb („Érdeklődés érkezett az applikációból” tárggyal).
- **Sötét mód** (alapból a telefon beállítását követi, a választás megmarad), kesztyűvel is nyomható, legalább 44–48 dp-s gombok.

## Webes app iPhone-ra (és bármilyen telefonra)

A `web/` mappában ugyanez a kalkulátor webes appként (PWA) is megvan. A **Webes app (GitHub Pages)** munkafolyamat minden változás után közzéteszi itt:

https://blaise04.github.io/fordulatszamkalkulator-cl/

- **iPhone:** nyisd meg Safariban → Megosztás gomb → „Főképernyőhöz adás”.
- **Android:** Chrome-ban megjelenik a „Telepítés” gomb (vagy menü → „Alkalmazás telepítése”).

Telepítés után saját ikonnal, teljes képernyőn fut, és internet nélkül is számol. Ha a webes appot frissíted, a `web/sw.js` fájlban a `CACHE` nevét is emeld (pl. `fkcl-v2`).

## APK készítése GitHubon

1. Hozz létre egy új GitHub repót, és töltsd fel ide a mappa teljes tartalmát (a `.github` mappával együtt).
2. Minden feltöltés (push) után automatikusan elindul az **Actions → APK build** munkafolyamat. Kézzel is indítható: *Actions → APK build → Run workflow*.
3. A futás végén a lap alján, az **Artifacts** részben letölthető:
   - `FordulatszamkalkulatorCL-teszt-apk` – teszt APK, mindig elkészül. Telefonra telepíthető kipróbáláshoz.
   - `FordulatszamkalkulatorCL-apk` – végleges, aláírt APK, ha be van állítva a kiadási kulcs (lásd lent). Ezt érdemes a weboldalra feltenni.

### Nyilvános letöltés (Releases)

Minden `main` ágra feltöltés után a build a **Releases** oldalra is kiteszi az APK-t, ahonnan bárki letöltheti, bejelentkezés nélkül (ehhez a repónak nyilvánosnak kell lennie):

- **Aláíró kulccsal:** végleges kiadás `FordulatszamkalkulatorCL.apk` néven. Az állandó, mindig a legfrissebb verzióra mutató link – ez mehet a weboldalra és QR-kódba:
  `https://github.com/Blaise04/fordulatszamkalkulator-cl/releases/latest/download/FordulatszamkalkulatorCL.apk`
- **Kulcs nélkül:** „teszt” előzetes kiadás (pre-release) `FordulatszamkalkulatorCL-teszt.apk` néven. Ez nem írja felül a fenti állandó linket.

### Kiadási aláíró kulcs (egyszer kell beállítani)

A végleges APK-t egy saját kulccsal kell aláírni. **Ezt a kulcsot őrizd meg biztonságos helyen** – frissítést csak ugyanazzal a kulccsal aláírt APK-val lehet kiadni.

```bash
keytool -genkeypair -v -keystore release.jks -alias fordulatszam \
  -keyalg RSA -keysize 2048 -validity 10000
base64 -w0 release.jks > release.jks.b64   # macOS-en: base64 -i release.jks -o release.jks.b64
```

Ezután a GitHub repóban: *Settings → Secrets and variables → Actions → New repository secret*:

| Secret neve | Értéke |
|---|---|
| `KEYSTORE_BASE64` | a `release.jks.b64` fájl tartalma |
| `KEYSTORE_PASSWORD` | a kulcstár jelszava |
| `KEY_ALIAS` | `fordulatszam` |
| `KEY_PASSWORD` | a kulcs jelszava |

A `release.jks` fájlt ne töltsd fel a repóba (a `.gitignore` ki is szűri).

## Fordítás Android Studióval

Nyisd meg a mappát Android Studióban (Ladybug vagy újabb). A projekt nem tartalmaz Gradle wrappert; ha a Studio kéri, engedd, hogy létrehozza, vagy futtasd egyszer: `gradle wrapper --gradle-version 8.9`. Ezután a *Build → Build APK(s)* menüponttal készül APK.

## Beállítások

| Mit | Hol |
|---|---|
| Telefonszám, email, tárgy, webshop cím | `app/src/main/java/.../Calc.kt` (`Calc` objektum) |
| Szerszámtípusok és max. kerületi sebességek | `Calc.kt` (`ToolType`) |
| Gyors átmérőgombok, szabványos méretek | `Calc.kt` (`QUICK_DIAMETERS`, `STANDARD_DIAMETERS`) |
| Színek | `Theme.kt` |
| App neve | `app/src/main/res/values/strings.xml` |

> A szerszámtípusonkénti max. kerületi sebességek tipikus értékek; kiadás előtt érdemes szakmailag ellenőrizni őket. A szerszámon feltüntetett érték mindig felülírja ezeket.

## Műszaki adatok

- Csomagazonosító: `hu.csiszolotarcsawebshop.fordulatszam`
- Minimum Android: 8.0 (API 26), cél: Android 15 (API 35)
- Engedélyek: nincsenek (a hívás gomb a tárcsázót nyitja meg, nem hív közvetlenül)
- Képletek: `n = v · 60 000 / (π · d)`, `v = π · d · n / 60 000`, `d = v · 60 000 / (π · n)`
