<template>
  <a-layout-sider :theme="theme">
    <a-menu mode="inline" :theme="theme" style="height: 100%"
            :default-open-keys="OPEN_MENU_IDS"
            :defaultSelectedKeys="SELECT_MENU_IDS">
      <template v-for="item in menus">
        <a-menu-item v-if="!item.children" :key="item.key">
           <svg class="iconfont" aria-hidden="true">
                <use :xlink:href="'#icon-'+item.icon"></use>
              </svg>
          <span>{{ item.name }}</span>
        </a-menu-item>

        <template v-else>
          <a-sub-menu :key="item.id">
            <span slot="title">
               <svg class="iconfont" aria-hidden="true">
                <use :xlink:href="'#icon-'+item.icon"></use>
              </svg>
              <span>{{ item.name }}</span>
            </span>
            <a-menu-item :key="item.key" v-for="item in item.children">
              <svg class="iconfont" aria-hidden="true">
                <use :xlink:href="'#icon-'+item.icon"></use>
              </svg>
              <span>{{ item.name }}</span>
            </a-menu-item>
          </a-sub-menu>
        </template>
      </template>
    </a-menu>
  </a-layout-sider>
</template>

<script>
// recommend use functional component
// <template functional>
//   <a-sub-menu :key="props.menuInfo.key">
//     <span slot="title">
//       <a-icon type="mail" /><span>{{ props.menuInfo.title }}</span>
//     </span>
//     <template v-for="item in props.menuInfo.children">
//       <a-menu-item v-if="!item.children" :key="item.key">
//         <a-icon type="pie-chart" />
//         <span>{{ item.title }}</span>
//       </a-menu-item>
//       <sub-menu v-else :key="item.key" :menu-info="item" />
//     </template>
//   </a-sub-menu>
// </template>
// export default {
//   props: ['menuInfo'],
// };
import {Menu} from 'ant-design-vue';

const SubMenu = {
  template: `
    <p>1213</p>
    <!--    <a-sub-menu :key="menuInfo.id" v-bind="$props" v-on="$listeners">-->
    <!--      <p>{{ menuInfo }}</p>-->
    <!--        <span slot="title">-->
    <!--          <a-icon type="mail"/><span>{{ menuInfo.name }}</span>-->
    <!--        </span>-->
    <!--      <template v-for="item in menuInfo.children">-->
    <!--        <a-menu-item v-if="!item.children" :key="item.key">-->
    <!--          <a-icon type="pie-chart"/>-->
    <!--          <span>{{ item.name }}</span>-->
    <!--        </a-menu-item>-->
    <!--        <sub-menu v-else :key="item.key" :menu-info="item"/>-->
    <!--      </template>-->
    <!--    </a-sub-menu>-->
  `,
  name: 'SubMenu',
  // must add isSubMenu: true
  isSubMenu: true,
  props: {
    ...Menu.SubMenu.props,
    // Cannot overlap with properties within Menu.SubMenu.props
    menuInfo: {
      type: Object,
      default: () => ({}),
    },
  },
  mounted() {
    console.log(this.menuInfo.name)
    console.log(SubMenu)
  }
};

export default {
  components: {
    'sub-menu': SubMenu,
  },
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
    };
  },
  methods: {},
};
</script>
