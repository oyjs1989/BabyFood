package com.example.babyfood.presentation.ui.common.legal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babyfood.presentation.theme.Background
import com.example.babyfood.presentation.theme.OnBackground
import com.example.babyfood.presentation.theme.OnSurface
import com.example.babyfood.presentation.theme.OnSurfaceVariant
import com.example.babyfood.presentation.theme.Primary
import com.example.babyfood.presentation.theme.Surface

/**
 * 隐私政策页面
 */
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit = {},
    onAgree: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "隐私政策",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = OnBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 最后更新时间
            Text(
                text = "最后更新：2026年1月1日",
                fontSize = 12.sp,
                color = OnSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 引言
            SectionTitle(title = "引言")
            SectionContent(text = """
                BabyFood（以下简称"我们"）非常重视您的隐私。本隐私政策旨在向您说明我们如何收集、使用、存储和保护您的个人信息。

                使用 BabyFood 应用程序（以下简称"本应用"）即表示您同意我们按照本隐私政策处理您的个人信息。
            """.trimIndent())

            // 1. 我们收集的信息
            SectionTitle(title = "1. 我们收集的信息")
            SectionContent(text = """
                1.1 账户信息
                • 手机号码或电子邮箱
                • 密码（加密存储）
                • 头像（可选）

                1.2 婴儿信息
                • 基本信息（姓名、性别、出生日期）
                • 健康数据（体重、身高、头围）
                • 体检记录（血液指标等）
                • 过敏信息
                • 偏好设置

                1.3 使用数据
                • 设备信息（设备型号、操作系统版本）
                • 应用使用日志
                • 菜单和食谱数据
                • 库存记录

                1.4 定位信息（可选）
                • 仅在您授权的情况下收集
                • 用于提供本地化内容

                1.5 Cookie 和类似技术
                • 用于改善用户体验
                • 用于分析应用使用情况
            """.trimIndent())

            // 2. 信息使用目的
            SectionTitle(title = "2. 信息使用目的")
            SectionContent(text = """
                我们使用收集的信息用于以下目的：

                2.1 提供和改进服务
                • 个性化食谱推荐
                • 营养分析和健康建议
                • 多设备数据同步

                2.2 账户管理
                • 用户身份验证
                • 账户安全保护
                • 密码重置

                2.3 沟通服务
                • 发送服务通知
                • 回应用户咨询
                • 提供客户支持

                2.4 数据分析
                • 分析应用使用情况
                • 改进产品功能
                • 优化用户体验

                2.5 法律合规
                • 遵守法律法规要求
                • 保护我们的合法权益
            """.trimIndent())

            // 3. 信息共享
            SectionTitle(title = "3. 信息共享")
            SectionContent(text = """
                除以下情况外，我们不会与第三方共享您的个人信息：

                3.1 获得您的同意
                • 在获得您明确同意的情况下共享

                3.2 服务提供商
                • 云服务提供商（用于数据存储和同步）
                • AI 服务提供商（用于智能推荐功能）
                • 通信服务提供商（用于发送验证码）

                3.3 法律要求
                • 遵守法律、法规或法院命令
                • 保护我们的权利、财产或安全
                • 保护用户或公众的安全

                3.4 业务转让
                • 在合并、收购或资产转让的情况下，信息可能会被转让给新的所有者

                3.5 敏感数据处理
                • 婴儿敏感信息（如姓名、出生日期）采用脱敏处理
                • 不在云端存储完整的敏感信息
            """.trimIndent())

            // 4. 信息存储和安全
            SectionTitle(title = "4. 信息存储和安全")
            SectionContent(text = """
                4.1 数据存储
                • 本地存储：使用加密数据库
                • 云端存储：使用加密传输和存储
                • 保留期限：您账户存在期间及合理时间内

                4.2 安全措施
                • 数据传输使用 SSL/TLS 加密
                • 密码使用 bcrypt 加密存储
                • 敏感信息脱敏处理
                • 定期安全审计
                • 访问权限控制

                4.3 数据备份
                • 定期备份数据
                • 备份数据同样加密存储
            """.trimIndent())

            // 5. 您的权利
            SectionTitle(title = "5. 您的权利")
            SectionContent(text = """
                根据相关法律法规，您享有以下权利：

                5.1 访问权
                • 查看我们持有的您的个人信息

                5.2 更正权
                • 更新或修正不准确的个人信息

                5.3 删除权
                • 要求删除您的个人信息（在法律允许的范围内）

                5.4 撤回同意
                • 撤回对特定数据处理活动的同意

                5.5 数据导出
                • 要求以结构化格式导出您的数据

                5.6 注销账户
                • 您可以随时注销账户，删除所有数据

                如需行使上述权利，请通过 support@babyfood.com 联系我们。
            """.trimIndent())

            // 6. Cookie 使用
            SectionTitle(title = "6. Cookie 使用")
            SectionContent(text = """
                我们使用 Cookie 和类似技术来：

                6.1 改善用户体验
                • 记住您的登录状态
                • 保存您的偏好设置

                6.2 分析应用使用
                • 了解用户如何使用应用
                • 识别应用性能问题

                您可以通过设备设置管理 Cookie，但这可能影响应用功能。
            """.trimIndent())

            // 7. 儿童隐私
            SectionTitle(title = "7. 儿童隐私")
            SectionContent(text = """
                7.1 我们特别注意保护儿童隐私
                • 我们不会故意收集 16 岁以下儿童的个人信息
                • 如需收集，将获得父母或监护人的同意

                7.2 如果您发现我们无意中收集了儿童的个人信息
                • 请立即联系我们
                • 我们将采取适当措施删除相关信息
            """.trimIndent())

            // 8. 隐私政策更新
            SectionTitle(title = "8. 隐私政策更新")
            SectionContent(text = """
                我们可能会不时更新本隐私政策。更新后的政策将在本应用上发布，并自发布之日起生效。

                重大变更时，我们会通过以下方式通知您：
                • 应用内通知
                • 邮件通知（如已提供）

                继续使用本应用即表示您接受更新后的隐私政策。
            """.trimIndent())

            // 9. 联系我们
            SectionTitle(title = "9. 联系我们")
            SectionContent(text = """
                如果您对本隐私政策有任何疑问或建议，请通过以下方式联系我们：

                • 邮箱：privacy@babyfood.com
                • 电话：400-123-4567
                • 地址：[您的公司地址]

                我们将在收到您的请求后尽快回复。
            """.trimIndent())

            Spacer(modifier = Modifier.height(32.dp))

            // 底部按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "返回",
                        fontSize = 14.sp,
                        color = OnSurface
                    )
                }

                Button(
                    onClick = onAgree,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "我同意",
                        fontSize = 14.sp,
                        color = Surface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = OnBackground,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
private fun SectionContent(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = OnSurface,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}