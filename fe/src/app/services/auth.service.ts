import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AppConstants} from '../common/AppConstants';
import {BehaviorSubject, catchError, map, Observable, of} from 'rxjs';
import {jwtDecode} from 'jwt-decode';


interface JwtPayload {
  sub: string;
  role: string;
  exp: number;
}

@Injectable({
  providedIn: 'root'
})

export class AuthService {

  private accessToken: string | null = null;
  /*
    A BehaviorSubject holds a value (true/false here) and can notify subscribers when it changes.
    isLoggedIn$ is an Observable that components can subscribe to or use with the async pipe.
    When login/logout happens, you call:
    this.isLoggedInSubject.next(true); // or false
  → Immediately notifies all components listening.
  This is powerful for reactive UI: the header, navbar, guards, etc. all stay in sync automatically.
   */
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();


  constructor(private http: HttpClient) {}


  login(identifier : String, password: String): Observable<any>  {
    return this.http.post(AppConstants.AUTH_API_URL + "login", {
      identifier, password
    } ,{withCredentials: true});
  }

  checkLoginStatus() {
    return this.http.get<boolean>(`${AppConstants.AUTH_API_URL}status`, {
      withCredentials: true  // <-- important: send cookies
    });
  }


  setLoginStatus(isLogged: boolean) {
    this.isLoggedInSubject.next(isLogged);
  }


  register(username: string, password: string, email: string): Observable<any> {
    return this.http.post(`${AppConstants.AUTH_API_URL}register`, {
      username, password, email
    }, {withCredentials: true});
  }

  setAccessToken(token: string): void {
    this.accessToken = token;
  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  clearAccessToken(): void {
    this.accessToken = null;
  }

  isLoggedAsAdmin() : boolean{
    console.log("is logged as admin function " + this.accessToken)
    if(!this.accessToken){
      return false;
    }
    try {
      console.log(this.accessToken)
      const payload = jwtDecode<JwtPayload>(this.accessToken);
      return payload.role === 'ADMIN'
    } catch (e) {
      return false;
    }
  }

  logout(): Observable<any> {
    return this.http.post(
      `${AppConstants.AUTH_API_URL}logout`,
      {},
      { withCredentials: true } // ensures cookie is included
    );
  }

  ensureAccessToken(): Observable<boolean> {
    console.log("im waiting for new access token")
    // call endpoint that validates refresh cookie and returns a fresh access token & role
    return this.http.post<{ success: boolean; data?: { accessToken: string, role: string } }>(
      `${AppConstants.AUTH_API_URL}new-access-token`,
      null,
      { withCredentials: true }
    ).pipe(
      map(res => {
        console.log(res);
        if (res?.success && res.data?.accessToken) {
          this.setAccessToken(res.data.accessToken);
          this.setLoginStatus(true);
          return true;
        }
        this.clearAccessToken();
        this.setLoginStatus(false);
        return false;
      }),
      catchError(() => {
        this.clearAccessToken();
        this.setLoginStatus(false);
        return of(false);
      })
    );
  }






}
