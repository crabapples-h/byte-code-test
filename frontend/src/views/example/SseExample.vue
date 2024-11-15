<template>
  <div>
    <a-space>
      <a-button type="primary" @click="scanDevice" :disabled="disable">开始扫描设备</a-button>
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
        connect: '/api/sse/connect',
        test: '/api/sse/send',
      },
      clientId: '',
      disable: false,
      messages: [],
      timer: null,
      connected: null,
      server: null,
      device: null
    }
  },
  mounted() {
  },
  watch: {
    connected(val, oldVal) {
      if (val) {
        console.log("设备已连接")
        this.$message.success('设备已连接')
        console.log('this.server=>', this.server)
        const services = this.server.getPrimaryServices()
        console.log('services=>', services)

        services.then(services => {
          console.log('services=>', services)
        }).catch(error => {
          console.error('Error: ' + error);
        })
      } else {
        console.log("设备断开连接")
        this.$message.warn('设备断开连接')
        this.device.gatt.connect().then(server => {

          if (server.connected) {
            this.server = server
            this.connected = true
          }
        })
      }
    }
  },
  methods: {
    scanDevice() {
      console.log("开始扫描设备")
      // console.log(navigator)
      if (typeof navigator.bluetooth === 'undefined') {
        console.log('您的浏览器不支持蓝牙API，请更换为Chrome浏览器')
        return
      }
      navigator.bluetooth.requestDevice({
        // acceptAllDevices: true,
        // filters: [{namePrefix: 'Xe'}],
        optionalServices: ['0000180c-0000-1000-8000-00805f9b34fb'],
        filters: [{namePrefix: 'crab'}]
      }).then(device => {
        console.log("选择的设备:", device)
        this.device = device
        this.connected = device.gatt.connected
        console.log("设备连接状态:", this.connected)
      })
      // //开始扫描设备
      // navigator.bluetooth.requestDevice(
      //     {
      //         // filters: [{namePrefix: prefix}],
      //         // optionalServices: serviceId
      //     }
      // ).then(device => {
      //   console.log(device)
      //
      // }).catch(error => {
      //     console.error('Error: ' + error);
      //     // addLogs('异常：连接设备失败。error: ' + error)
      //     // hideOverlay();
      //     // showErrorToast('连接失败，请查看日志信息')
      // });
      // }


      // this.clientId = Math.floor(Math.random() * 10000000).toString()
      // let url = `${this.url.connect}/${this.clientId}`;
      // // console.log(NativeEventSource)
      // // console.log(EventSourcePolyfill)
      // let connection = new EventSourcePolyfill(url, {
      //   headers: this.headers
      // });
      // console.log("url", url, connection);
      //
      // connection.addEventListener("open", (event) => {
      //   // this.disable = true;
      //   console.log("连接成功", event);
      // });
      // connection.addEventListener("log", (event) => {
      //   console.log("收到消息", event.data);
      //   this.messages.push(event.data)
      // });
      // connection.addEventListener("error", (event) => {
      //   console.warn("连接断开", event);
      //   this.disable = false;
      //   // sse.close();
      // });
      // this.testSend()
    },
    testSend() {
      setTimeout(() => {
        this.$http.post(`${this.url.test}/${this.clientId}`)
      }, 2000)
    }
  }
}

</script>

<style scoped>


</style>