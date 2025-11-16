export class AppConstants {
  private static API_BASE_URL = "http://localhost:8080/";
  private static OAUTH2_URL = AppConstants.API_BASE_URL + "oauth2/authorization/";
  public static API_URL = AppConstants.API_BASE_URL + "api/";
  public static AUTH_API_URL = AppConstants.API_URL + "auth/";
  public static GOOGLE_AUTH_URL = AppConstants.OAUTH2_URL + "google";
}
