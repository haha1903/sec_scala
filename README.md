# 品专重构
## 周期
- 一个月
## 提升
- 重复代码比例
- 测试覆盖率、分层补充 dao -> service
- Blocker, Critical 问题排查
- 降低 Code Complexity
- 保持自动化测试比例
## 预期
- 总代码行数下降
- 品专 KA 单独部署
- 功能、性能、稳定性保持不变
## 过程影响
- 减少新 Story 响应
## 一期范围
- DB 不变
- 外部依赖不变
- KA 从 JN 迁出，与 PLP 放在一起
- FE -> namespace 变化
- package -> KA, PLP 单独打包
## 质量保证
- 补充单测覆盖业务逻辑 dao -> service
- QA(RD)全量回归，配合QA自动测试？Test Case 整理
## 上线
- 灰度上线
## 重构后架构
- Framework 变化，KA -> NMP
## 步骤
- 代码从 JN 分离，合并为一个项目
- 在 Mgr 以下，将项目实现分开
- 尽量合并 mgr 接口，复用 Action 逻辑
- 合并前端展示共用的 Bean
- 提取公共 util 复用下层逻辑
- 公用的外部调用
## 关联系统变化
- ContractLine, User 同步基于 NMP，原来是 RPC -> JMS
