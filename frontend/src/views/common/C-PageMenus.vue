<template>
  <a-layout-sider :theme="theme">
    <a-menu mode="inline" :theme="theme" style="height: 100%"
            :default-open-keys="OPEN_MENU_IDS"
            :defaultSelectedKeys="SELECT_MENU_IDS">
      <a-sub-menu :key="item.id" v-for="item in menus" v-if="item.children && item.children.length">
        <span slot="title"><a-icon :type="item.icon"/><span>{{ item.name }}</span></span>
        <a-sub-menu :key="item.id" v-if="item.children && item.children.length" v-for="item in item.children">
          <span slot="title"><a-icon :type="item.icon"/><span>{{ item.name }}</span></span>
          <a-menu-item :key="item.id" v-for="item in item.children" @click="click(item)">
            <a-icon :type="item.icon" v-if="item.icon"/>
            <span>{{ item.name }}</span>
            <span>{{ item.icon }}</span>
          </a-menu-item>
        </a-sub-menu>
        <a-menu-item :key="item.id" v-else @click="click(item)">
          <a-icon :type="item.icon" v-if="item.icon"/>
          <span>{{ item.name }}</span>
        </a-menu-item>
      </a-sub-menu>
      <a-menu-item :key="item.id" v-else @click="click(item)">
        <a-icon :type="item.icon" v-if="item.icon"/>
        <span>{{ item.name }}</span>
      </a-menu-item>
    </a-menu>
  </a-layout-sider>
</template>

<script>

export default {
  name: 'C-PageMenus',
  props: {
    theme: {
      type: String,
    },
    menus: {
      type: Array,
      required: true,
      default: () => {
        return []
      }
    },
    clickMenu: {
      type: Function,
    },
  },
  data() {
    return {
      OPEN_MENU_IDS: [localStorage.getItem('OPEN_MENU_IDS')],
      SELECT_MENU_IDS: [localStorage.getItem('SELECT_MENU_IDS')],
    }
  },
  beforeCreate() {
  },
  activated() {
  },
  mounted() {
    console.log(this.OPEN_MENU_IDS)
  },
  methods: {
    click(e) {
      localStorage.setItem('OPEN_MENU_IDS', e.pid)
      localStorage.setItem('SELECT_MENU_IDS', e.id)
      this.$emit('clickMenu', e)
    },
  }
}
</script>

<style scoped lang="less">
@import "~@public/color.less";
</style>
