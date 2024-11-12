import {initCPagination} from '@/views/common/C-Pagination'
import {TreeSelect} from 'ant-design-vue'
import CButton from '@comp/c-button.vue'
import CPopButton from '@comp/c-pop-button.vue'
import formRules from '@/utils/formRules'

export default {
    components: {
        CButton,
        CPopButton,
    },
    data() {
        return {
            formRules: formRules,
            SHOW_TYPE: TreeSelect.SHOW_CHILD,
            labelCol: {span: 5},
            wrapperCol: {span: 16},
            pagination: initCPagination(this.changeIndex, this.changeSize),
            dataSource: [],
            url: {
                list: ''
            },
            form: {},
            spinning: false,
            queryParam: {},
            scrollY: 500
        }
    },
    watch: {
        dataSource() {
            this.scrollY = getTableScroll();
        }
    },
    created() {
    },
    activated() {
        this.getList()
    },
    mounted() {
    },
    methods: {
        getTableScroll(extraHeight = 74, id) {
            let tHeader = null
            if (id) {
                tHeader = document.getElementById(id) ? document.getElementById(id).getElementsByClassName("ant-table-thead")[0] : null
            } else {
                tHeader = document.getElementsByClassName("ant-table-thead")[0]
            }
            //表格内容距离顶部的距离
            let tHeaderBottom = 0
            if (tHeader) {
                tHeaderBottom = tHeader.getBoundingClientRect().bottom
            }
            const windowHeight = document.documentElement.clientHeight
            //窗体高度-表格内容顶部的高度-表格内容底部的高度
            let height = windowHeight - tHeaderBottom - extraHeight
            return height
        },
        resetSearch() {
            this.queryParam = {}
            this.refreshData()
        },
        listToTree(list, pidKey, pid = null) {
            const tree = []
            for (const item of list) {
                if (item.pid === pid) {
                    const children = this.listToTree(list, item.id)
                    if (children.length) {
                        item.children = children
                    }
                    tree.push(item)
                }
            }
            return tree
        },
        getQueryPage() {
            return {
                pageIndex: this.pagination.current,
                pageSize: this.pagination.pageSize
            }
        },
        changeIndex(pageIndex, pageSize) {
            this.pagination.current = pageIndex
            this.pagination.pageSize = pageSize
            this.getList()
        },
        changeSize(pageIndex, pageSize) {
            this.pagination.current = pageIndex
            this.pagination.pageSize = pageSize
            this.getList()
        },

        getList() {
            if (!!this.url.list) {
                let config = {
                    params: Object.assign(this.queryParam, this.getQueryPage())
                }
                this.$http.get(this.url.list, config).then(result => {
                    if (result.status !== 200) {
                        this.$message.error(result.message)
                        return
                    }
                    if (result.data !== null) {
                        this.dataSource = result.data.records || result.data
                        this.dataSource = this.dataSource.sort((a, b) => {
                            return a.sort - b.sort
                        })
                        // if (result.data.pageable) {
                        this.pagination.total = result.data.total
                        this.pagination.current = result.data.current
                        this.pagination.pageSize = result.data.size
                        // }
                    }
                }).catch(function (error) {
                    console.error('出现错误:', error)
                })
            } else {
                console.log('组件[%s]的[url.list]为空', this.$route.path)
            }
        },
        remove(e) {
            this.$http.post(`${this.url.delete}/${e.id}`).then(result => {
                if (result.status !== 200) {
                    this.$message.error(result.message)
                    return
                }
                this.$message.success(result.message)
            }).catch(function (error) {
                console.log('请求出现错误:', error)
            }).finally(() => {
                this.refreshData()
            })
        },
        refreshData() {
            this.resetForm()
            this.getList()
        },
        resetForm() {
            this.form = {}
        },
    }
}
