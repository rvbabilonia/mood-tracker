import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { retry, catchError, tap, map } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { Mood } from './mood';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  apiURL = environment.baseUrl;

  constructor(private http: HttpClient) { }

  httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      withCredentials: true
    };

  createMood(mood): Observable<any> {
    return this.http.post<any>(this.apiURL + '/api/v1/moods', JSON.stringify(mood), this.httpOptions)
    .pipe(
      catchError(this.handleError<any>())
    );
  }

  private handleError<T>(result?: T) {
    return (error: any): Observable<T> => {
      window.alert(error.error.message);

      return of(result as T);
    };
  }
}
