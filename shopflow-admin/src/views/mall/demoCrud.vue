<template>
  <div class="app-container">
    <div class="filter-container">
      <el-form :model="listQuery" size="small" :inline="true">
        <el-form-item label="问题关键字">
          <el-input
            v-model="listQuery.question"
            clearable
            class="filter-item"
            style="width: 240px"
            placeholder="输入问题关键字"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleFilter">查询</el-button>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row>
      <el-table-column align="center" label="ID" prop="id" width="100" />
      <el-table-column align="center" label="问题" prop="question" min-width="220" />
      <el-table-column align="center" label="回复" prop="answer" min-width="320" />
      <el-table-column align="center" label="操作" width="180">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible">
      <el-form ref="formRef" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="问题" prop="question">
          <el-input v-model="form.question" />
        </el-form-item>
        <el-form-item label="回复" prop="answer">
          <el-input v-model="form.answer" type="textarea" :rows="6" />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import {
  listDemoCrud,
  createDemoCrud,
  updateDemoCrud,
  deleteDemoCrud
} from '@/api/demoCrud'

export default {
  name: 'DemoCrud',
  components: { Pagination },
  data() {
    return {
      listLoading: false,
      list: [],
      total: 0,
      listQuery: {
        page: 1,
        limit: 10,
        question: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      dialogVisible: false,
      mode: 'create',
      form: {
        id: undefined,
        question: '',
        answer: ''
      },
      rules: {
        question: [{ required: true, message: '请输入问题', trigger: 'blur' }],
        answer: [{ required: true, message: '请输入回复', trigger: 'blur' }]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.mode === 'create' ? '新增数据' : '编辑数据'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        const res = await listDemoCrud(this.listQuery)
        this.list = res.data.list
        this.total = res.data.total
      } finally {
        this.listLoading = false
      }
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleCreate() {
      this.mode = 'create'
      this.form = {
        id: undefined,
        question: '',
        answer: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.formRef.clearValidate())
    },
    handleUpdate(row) {
      this.mode = 'update'
      this.form = {
        id: row.id,
        question: row.question,
        answer: row.answer
      }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.formRef.clearValidate())
    },
    submitForm() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        if (this.mode === 'create') {
          await createDemoCrud(this.form)
          this.$message.success('新增成功')
        } else {
          await updateDemoCrud(this.form)
          this.$message.success('更新成功')
        }
        this.dialogVisible = false
        this.getList()
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除这条数据吗？', '提示', { type: 'warning' })
        .then(async () => {
          await deleteDemoCrud(row.id)
          this.$message.success('删除成功')
          this.getList()
        })
        .catch(() => {})
    }
  }
}
</script>
