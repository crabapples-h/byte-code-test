<template>
  <div>
    <a-form layout="inline" @keyup.enter.native="getList">
      <a-space>
        <a-form-item label="名称">
          <a-input placeholder="请输入字典名称" v-model="queryParam.name" :allow-clear="true"/>
        </a-form-item>
        <a-button type="default" @click="getList" icon="search">查询</a-button>
        <a-button type="default" @click="resetSearch" icon="reload">重置</a-button>
        <a-button type="primary" @click="showAdd" icon="plus" v-auth:sys:menus:add ghost>添加</a-button>
      </a-space>
    </a-form>
    <a-divider/>
    <dict-detail :visible="show.detail" :dict-code="dictCode" @cancel="closeDetail"/>
    <add-dict :visible="show.add" @cancel="closeForm" :is-edit="show.edit" ref="AddDict"/>
    <add-dict-item :visible="show.addItem" :dict-code="dictCode" @cancel="closeItemForm"/>
    <a-table :data-source="dataSource" rowKey="id" :columns="columns" :pagination="pagination"
             :scroll="{x:true}" bordered>
      <span slot="action" slot-scope="text, record">
        <a-space>
        <c-pop-button title="确定要删除吗" text="删除" type="danger" @click="remove(record)"/>
        <a-button type="primary" size="small" @click="showEdit(record)">编辑</a-button>
        <a-button type="primary" size="small" @click="showAddItem(record)">添加字典项</a-button>
        <a-button type="primary" size="small" @click="showDetail(record)">查看字典项</a-button>
          </a-space>
      </span>
      <span slot="icon" slot-scope="text, record">
        <a-icon :type='text.substring(text.indexOf("\"") + 1,text.lastIndexOf("\"")) || "appstore"'/>
      </span>
    </a-table>
  </div>
</template>

<script>

import {SysApis} from '@/api/Apis'
import SystemMinix from '@/minixs/SystemMinix'
import AddDict from '@/views/manage/sys-dict/add.vue'
import AddDictItem from '@/views/manage/sys-dict/add-item.vue'
import DictDetail from '@/views/manage/sys-dict/detail.vue'

export default {
  name: 'menus-list',
  mixins: [SystemMinix],
  components: {
    AddDict,
    AddDictItem,
    DictDetail
  },
  data() {
    return {
      columns: [
        {
          dataIndex: 'name',
          title: '名称',
        },
        {
          dataIndex: 'code',
          title: '代码',
        },
        {
          dataIndex: 'action',
          title: '操作',
          scopedSlots: {customRender: 'action'},
          width: 200
        },
      ],
      dataSource: [],
      show: {
        add: false,
        edit: false,
        detail: false,
      },
      url: {
        list: SysApis.dictPage,
        delete: SysApis.delDicts,
      },
      dictCode: '',
    }
  },
  activated() {
  },
  mounted() {
  },
  methods: {
    showAdd() {
      this.show.add = true
    },
    closeForm() {
      this.show.add = false
      this.show.edit = false
      this.refreshData()
    },
    showEdit(e) {
      this.$refs.AddDict.form = e
      this.show.add = true
      this.show.edit = true
    },
    showAddItem(e) {
      this.dictCode = e.id
      this.show.addItem = true
    },
    closeItemForm() {
      this.show.addItem = false
      this.refreshData()
    },
    showDetail(e) {
      this.dictCode = e.code
      this.show.detail = true
    },

    closeDetail() {
      this.show.detail = false
    },
    showEditItem(e) {
      this.itemForm = e
      this.show.addItem = true
    },

    submitAddItemForm() {
      this.$refs.dictItemForm.validate(valid => {
        if (valid) {
          this.$http.post(this.url.saveDictItems, this.itemForm).then(result => {
            if (result.status !== 200) {
              this.$message.error(result.message)
              return
            }
            this.closeAddItem()
          }).catch(function (error) {
            console.error('出现错误:', error)
          })
        }
      })
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
