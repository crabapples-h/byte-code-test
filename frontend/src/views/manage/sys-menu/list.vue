<template>
  <div>
    <a-form layout="inline" @keyup.enter.native="getList">
      <a-space>
        <a-form-item label="菜单">
          <a-input placeholder="请输入菜单" v-model="queryParam.name" :allow-clear="true"/>
        </a-form-item>
        <a-button type="default" @click="getList" icon="search">查询</a-button>
        <a-button type="default" @click="resetSearch" icon="reload">重置</a-button>
        <a-button type="primary" @click="showAdd" icon="plus" v-auth:sys:menus:add ghost>添加</a-button>
      </a-space>
    </a-form>
    <a-divider/>
    <a-table :data-source="dataSource" rowKey="id" :columns="columns" :pagination="pagination"
             :scroll="{x:true}" bordered>
      <span slot="action" slot-scope="text, record">
        <a-space>
        <c-pop-button title="确定要删除吗" text="删除" type="danger" @click="remove(record)" v-auth:sys:menus:del/>
        <a-button type="primary" size="small" @click="showEdit(record)" v-auth:sys:menus:edit>编辑</a-button>
        <span v-if="record.menusType === 1">
          <a-button type="primary" size="small" @click="showAddChild(record)"
                    v-auth:sys:menus:add-children>添加子菜单</a-button>
        </span>
        </a-space>
      </span>
      <span slot="icon" slot-scope="text, record">
        <a-icon :type='text.substring(text.indexOf("\"") + 1,text.lastIndexOf("\"")) || "appstore"'/>
      </span>
      <span slot="type" slot-scope="text, record">
        <a-tag size="small" color="green" v-if="record.menusType === 1">菜单</a-tag>
        <a-tag size="small" color="blue" v-if="record.menusType === 2">按钮</a-tag>
        <a-tag size="small" color="purple" v-if="record.menusType === 3">超链接</a-tag>
      </span>
    </a-table>
    <add-menu :visible="show.add" @cancel="closeForm" ref="addMenu"/>
  </div>
</template>

<script>

import commonApi from '@/api/CommonApi'
import {SysApis} from '@/api/Apis'
import SystemMinix from '@/minixs/SystemMinix'
import AddMenu from '@/views/manage/sys-menu/add.vue'

export default {
  name: 'menu-list',
  mixins: [SystemMinix],
  components: {
    AddMenu
  },
  data() {
    return {
      columns: [
        {
          dataIndex: 'name',
          title: '名称',
          align: 'center',
          width: 200
        },
        {
          dataIndex: 'icon',
          title: '图标',
          scopedSlots: {customRender: 'icon'},
          align: 'center',
          width: 80
        },
        {
          dataIndex: 'sort',
          title: '排序',
          align: 'center',
          width: 80
        },
        {
          dataIndex: 'type',
          title: '类型',
          scopedSlots: {customRender: 'type'},
          align: 'center',
          width: 80
        },
        {
          dataIndex: 'permission',
          title: '授权标识',
        },
        {
          title: '操作',
          key: 'action',
          scopedSlots: {customRender: 'action'},
          width: 200
        }
      ],
      dataSource: [],
      show: {
        add: false,
      },
      url: {
        list: SysApis.menuPage,
        delete: SysApis.delMenus,
      }
    }
  },
  activated() {
  },
  mounted() {
  },
  methods: {
    getList() {
      let page = this.getQueryPage()
      this.$http.get(this.url.list, {params: page}).then(result => {
        if (result.status !== 200) {
          this.$message.error(result.message)
          return
        }
        if (result.data !== null) {
          let format = function (data) {
            return data.map(e => {
              let menus = {
                id: e.id,
                key: e.id,
                name: e.name,
                icon: e.icon,
                url: e.path,
                sort: e.sort,
                menusType: e.menusType,
                path: e.path,
                link: e.link,
                filePath: e.filePath,
                permission: e.permission,
                showFlag: e.showFlag,
              }
              if (e.children && e.children.length > 0) {
                menus.children = format(e.children)
              }
              return menus
            }).sort((a, b) => {
              return a.sort - b.sort
            })
          }
          this.dataSource = format(result.data.records)
          this.pagination.total = result.data.total
          this.pagination.current = result.data.current
          this.pagination.pageSize = result.data.size
        }
      }).catch(function (error) {
        console.error('出现错误:', error)
      })
    },
    showAdd() {
      this.show.add = true
    },
    showAddChild(e) {
      this.$refs.addMenu.form.pid = e.id
      this.show.add = true
    },
    showEdit(e) {
      this.$refs.addMenu.form = e
      this.show.add = true
    },
    closeForm() {
      this.show.add = false
      this.refreshData()
      commonApi.refreshSysData()
    },
  }
}
</script>

<style scoped>
.drawer-bottom-button {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 100%;
  border-top: 1px solid #e9e9e9;
  padding: 10px 16px;
  background: #fff;
  text-align: right;
  z-index: 1;
}
</style>
