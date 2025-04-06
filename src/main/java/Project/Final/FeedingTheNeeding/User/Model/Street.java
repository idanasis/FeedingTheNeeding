package Project.Final.FeedingTheNeeding.User.Model;
public enum Street {
   HAIR_HAATIKA("העיר העתיקה"),
   DAROM("דרום"),
   NEVE_OFER("נווה עופר"),
   HAMERKAZ_HAIZRACHI("המרכז האזרחי"),
   ALEF("א'"),
   BET("ב'"),
   GIMEL("ג'"),
   DALET("ד'"),
   HEY("ה'"),
   VAV("ו'"),
   TET("ט'"),
   YUD_ALEF("י\"א"),
   NEOT_LON("נאות לון"),
   NEVE_ZEEV("נווה זאב"),
   NEVE_NOY("נווה נוי"),
   NACHAL_BEQA("נחל בקע"),
   NACHAL_ASHAN("נחל עשן (נווה מנחם)"),
   RAMOT("רמות"),
   NEOT_AVRAHAM("נאות אברהם (פלח 6)"),
   NEVE_ILAN("נווה אילן (פלח 7)"),
   ROBA_HAHIDSHANUT("רובע החדשנות"),
   KIRYAT_GANIM("קריית גנים"),
   HAKALANIYOT("הכלניות"),
   SIGALIYOT("סיגליות"),
   PARK_HANACHAL("פארק הנחל");

   private final String hebrewName;

   Street(String hebrewName) {
      this.hebrewName = hebrewName;
   }

   public String getHebrewName() {
      return hebrewName;
   }
   public static Street fromHebrewName(String hebrewName) {
      for (Street street : Street.values()) {
         if (street.getHebrewName().equals(hebrewName)) {
            return street;
         }
      }
      throw new IllegalArgumentException("No enum constant with hebrewName: " + hebrewName);
   }
}

