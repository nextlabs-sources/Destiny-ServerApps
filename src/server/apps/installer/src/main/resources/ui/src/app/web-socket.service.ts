import {Injectable} from '@angular/core';
import {SERVER_URL} from "./installer-constants";
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  stompClient;
  constructor(){
  }

  connect(onMessageReceived) {
    let socket = new SockJS(`${SERVER_URL}/cc-installation-progress`);
    this.stompClient = Stomp.over(socket);

    // disable debug logging
    this.stompClient.debug = () => {};
    this.stompClient.connect({}, (frame) => {
      this.stompClient.subscribe('/topic/progress', onMessageReceived);
    });
  }

  disconnect(){
    this.stompClient.disconnect();
    console.log("websocket disconnected");
  }
}
