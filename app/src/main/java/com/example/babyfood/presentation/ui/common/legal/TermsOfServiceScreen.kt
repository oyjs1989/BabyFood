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
import androidx.compose.material3.MaterialTheme
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
 * 服务条款页面
 */
@Composable
fun TermsOfServiceScreen(
    onBack: () -> Unit = {},
    onAgree: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "服务条款",
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

            // 1. 服务条款接受
            SectionTitle(title = "1. 服务条款接受")
            SectionContent(text = """
                欢迎使用 BabyFood 应用程序（以下简称"本应用"）。通过访问、浏览或使用本应用，您确认您已阅读、理解并同意遵守本服务条款。

                如果您不同意本服务条款的任何部分，请勿使用本应用。
            """.trimIndent())

            // 2. 服务描述
            SectionTitle(title = "2. 服务描述")
            SectionContent(text = """
                BabyFood 是一个婴幼儿辅食管理应用程序，提供以下服务：

                • 食谱管理和推荐
                • 营养目标计算和追踪
                • 健康记录和生长曲线分析
                • 多设备数据同步
                • AI 智能推荐功能

                我们保留随时修改、暂停或终止服务的权利，无需事先通知。
            """.trimIndent())

            // 3. 用户账户
            SectionTitle(title = "3. 用户账户")
            SectionContent(text = """
                3.1 要使用本应用，您必须创建一个账户。您有责任维护您账户信息的准确性和安全性。

                3.2 您同意对您账户下发生的所有活动负责。如果您发现任何未经授权使用您账户的情况，请立即通知我们。

                3.3 您不得创建虚假账户或使用他人的账户信息。
            """.trimIndent())

            // 4. 用户责任
            SectionTitle(title = "4. 用户责任")
            SectionContent(text = """
                使用本应用时，您同意：

                4.1 遵守所有适用的法律法规
                4.2 不使用本应用进行任何非法或未经授权的目的
                4.3 不传播病毒、恶意代码或任何可能损害本应用的程序
                4.4 不侵犯他人的知识产权、隐私权或其他权利
                4.5 不骚扰、威胁或滥用其他用户
            """.trimIndent())

            // 5. 知识产权
            SectionTitle(title = "5. 知识产权")
            SectionContent(text = """
                本应用及其内容（包括但不限于文本、图形、图像、软件、代码等）受知识产权法保护。

                您不得复制、修改、分发、展示、执行或以其他方式使用本应用的任何部分，除非获得我们的明确书面许可。
            """.trimIndent())

            // 6. 免责声明
            SectionTitle(title = "6. 免责声明")
            SectionContent(text = """
                6.1 本应用提供的营养建议、食谱推荐和健康分析仅供参考，不构成医疗建议。

                6.2 我们不对因使用本应用或本应用中的信息而导致的任何直接或间接损失负责。

                6.3 本应用可能包含来自第三方的链接，我们不对这些第三方网站的内容或服务负责。

                6.4 我们不保证本应用将不间断、及时、安全或无错误。
            """.trimIndent())

            // 7. 隐私保护
            SectionTitle(title = "7. 隐私保护")
            SectionContent(text = """
                我们重视您的隐私。关于我们如何收集、使用和保护您的个人信息，请参阅我们的《隐私政策》。

                使用本应用即表示您同意我们按照《隐私政策》处理您的个人信息。
            """.trimIndent())

            // 8. 服务变更和终止
            SectionTitle(title = "8. 服务变更和终止")
            SectionContent(text = """
                8.1 我们保留随时修改或中断服务的权利，无需对您或任何第三方承担责任。

                8.2 我们可能因以下原因终止您的账户：
                • 违反本服务条款
                • 提供虚假信息
                • 滥用本应用
                • 长期未使用账户

                8.3 账户终止后，您将无法访问本应用的服务。
            """.trimIndent())

            // 9. 服务条款修改
            SectionTitle(title = "9. 服务条款修改")
            SectionContent(text = """
                我们保留随时修改本服务条款的权利。修改后的条款将在本应用上发布，并自发布之日起生效。

                继续使用本应用即表示您接受修改后的服务条款。
            """.trimIndent())

            // 10. 联系我们
            SectionTitle(title = "10. 联系我们")
            SectionContent(text = """
                如果您对本服务条款有任何疑问，请通过以下方式联系我们：

                • 邮箱：support@babyfood.com
                • 电话：400-123-4567

                感谢您使用 BabyFood！
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