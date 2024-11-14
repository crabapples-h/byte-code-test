<template>
  <div>
    <a-space>
      <a-button type="primary" @click="connect" :disabled="disable">开始连接</a-button>
      <a-button type="primary" @click="connect1" :disabled="disable">开始连接1</a-button>
    </a-space>
    <p v-for="item in messages">{{ item }}</p>
  </div>

</template>

<script>
import {NativeEventSource, EventSourcePolyfill} from 'event-source-polyfill';
import system from '@/mixins/system'

export default {
  name: "SseExample",
  mixins: [system],
  data() {
    return {
      url: {
        // sse: '/api/sse/connect',
        sse: '/videoability/chargingStation/api/sse/connect',
        test: '/api/sse/send',
      },
      clientId: '',
      disable: false,
      messages: [],
      timer: null
    }
  },
  methods: {
    connect() {
      // this.clientId = Math.floor(Math.random() * 10000000).toString()
      this.clientId = "123"
      let url = `${this.url.sse}/${this.clientId}`;
      // console.log(NativeEventSource)
      // console.log(EventSourcePolyfill)
      let sse = new EventSourcePolyfill(url, {
        headers: this.headers
      });
      console.log("url", url, sse);

      sse.addEventListener("open", (event) => {
        // this.disable = true;
        console.log("连接成功", event);
      });
      sse.addEventListener("message", (event) => {
        console.log("收到消息", event);
      });
      sse.addEventListener("error", (event) => {
        console.warn("连接断开", event);
        this.disable = false;
        // sse.close();
      });
      // this.testSend()
    },
    connect1() {
      // this.clientId = Math.floor(Math.random() * 10000000).toString()
      this.clientId = "1231"
      let url = `${this.url.sse}/${this.clientId}`;
      // console.log(NativeEventSource)
      // console.log(EventSourcePolyfill)
      const eventSource = new EventSource(url);
      eventSource.onopen = function (event) {
        this.disable = true;
        console.log("连接成功");
        // this.timer = setInterval(() => {
        //   this.testSend()
        // }, 2000)
      };
      eventSource.onmessage = function (event) {
        this.message = event.data;
        let message = event.data;
        console.log("消息推送->", message);
      };
      eventSource.onerror = function (error) {
        console.error("连接端口:", error);
        this.disable = false;
        // clearInterval(this.timer)
        // this.timer = null
        eventSource.close();
      };
    },
    testSend() {
      this.$http.post(`${this.url.test}/${this.clientId}`)
    }
  }
}

</script>

<style scoped>


</style>