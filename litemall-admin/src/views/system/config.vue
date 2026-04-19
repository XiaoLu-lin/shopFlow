<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24" :xs="24">
        <el-card>
          <div slot="header" class="clearfix">
            <span>配置中心</span>
          </div>
          <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane :label="item.name" :name="item.prefix" v-for="(item, index) in configGroupList" :key="index">
              <!-- 查询结果 -->
              <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="handleSelectionChange">

                <el-table-column align="center" label="管理员ID" prop="adminId" :show-overflow-tooltip="true"/>

                <el-table-column align="center" label="配置名称" prop="name" :show-overflow-tooltip="true"/>

                <el-table-column align="center" label="配置描述" prop="depict" :show-overflow-tooltip="true"/>

                <el-table-column align="center" label="配置值" prop="value" :show-overflow-tooltip="true"/>

                <el-table-column align="center" label="配置值单位" prop="unit" :show-overflow-tooltip="true"/>

                <el-table-column align="center" label="更新日期" prop="updateTime" :show-overflow-tooltip="true"/>

                <el-table-column align="center" label="操作">
                  <template slot-scope="scope">
                    <el-button v-hasPermi="['POST /admin/config/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 80%; margin-left:50px;">
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="dataForm.name" disabled/>
        </el-form-item>

        <el-form-item label="配置描述" prop="depict">
          <el-input v-model="dataForm.depict" disabled/>
        </el-form-item>

        <el-form-item label="值类型" prop="objectType">
          <el-input v-model="dataForm.objectType" disabled/>
        </el-form-item>

        <el-form-item label="配置值" prop="value">
          <el-input v-model="dataForm.value">
            <template slot="append" v-if="dataForm.unit">{{dataForm.unit}}</template>
          </el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus ==='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

import { getEnums } from '@/api/enums'
import { listConfig, updateConfig } from '@/api/config'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // 基于el-分页的辅助包
import _ from 'lodash'

export default {
  name: 'Config',
  components: { BackToTop, Pagination },
  data() {
    return {
      list: [],
      total: 0,
      showSearch: true,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 50,
        name: 'system',
        sort: 'add_time',
        order: 'desc'
      },
      dataForm: {
        id: undefined,
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {},
      multiple: false,
      multipleSelection: [],
      downloadLoading: false,
      activeName: 'system',
      configGroupList: [],
    }
  },
  created() {
    this.getList();
    //获取枚举列表
    getEnums("configGroup").then(response => {
      this.configGroupList = response.data.list;
    })
  },
  methods: {


    handleClick(tab, event) {
      this.listQuery.name = tab.name;
      this.getList();
    },

    getList() {
      this.listLoading = true
      listConfig(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = response.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },

    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },

    resetForm() {
      this.dataForm = {
        id: undefined,
      }
    },

    //获取文件上传回调
    uploadInputImage(res){
      this.dataForm.configUrl = res;
    },

    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },

    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          createConfig(this.dataForm).then(response => {
            this.dialogFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '添加成功'
            })
            this.getList()
          })
        }
      })
    },

    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },

    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          updateConfig(this.dataForm).then(() => {
            this.dialogFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '更新成功'
            })
            this.getList();
          })
        }
      })
    },


    handleDelete(row) {
      this.$confirm('确定删除?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      }).then(() => {
        deleteConfig(row.id).then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除成功'
          })
          this.getList()
        })
      }).catch(() => {})
    },

    handleSelectionChange(val) {
      this.multipleSelection = val
    },

    handleBatchDelete() {
      if (this.multipleSelection.length === 0) {
        this.$message.error('请选择至少一条记录')
        return
      }
      const ids = []
      _.forEach(this.multipleSelection, function(item) {
        ids.push(item.id)
      })

      this.$confirm('确定批量删除?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      }).then(() => {
        batchDeleteConfig({ ids: ids }).then(response => {
          this.$notify.success({
            title: '成功',
            message: '批量删除成功'
          })
          this.getList()
        })
      }).catch(() => {})
    },

    handleDownload() {
      this.downloadLoading = true
      import('@/utils/vendor/Export2Excel').then(excel => {
        const tHeader = ['用户序号', '商品序号', '添加时间']
        const filterVal = ['userId', 'valueId', 'addTime']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '用户收藏信息')
        this.downloadLoading = false
      })
    }


  }
}
</script>
