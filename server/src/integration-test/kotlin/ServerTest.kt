import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.test.core.TestProjectApi
import com.test.core.humanstandardtoken.model.ApproveParameters
import com.test.core.humanstandardtoken.model.HumanStandardTokenDeployParameters
import com.test.wrappers.HumanStandardToken
import org.glassfish.jersey.test.JerseyTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.NodeType
import org.web3j.openapi.client.ClientFactory
import org.web3j.openapi.client.ClientService
import org.web3j.openapi.server.config.OpenApiResourceConfig
import org.web3j.openapi.server.config.OpenApiServerConfig
import org.web3j.protocol.Web3j
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger
import java.net.URL
import javax.ws.rs.core.Application

@EVMTest(type = NodeType.BESU)
class ServerTest : JerseyTest() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }
    
    private val client: TestProjectApi by lazy {
        ClientFactory.create(
            TestProjectApi::class.java,
            ClientService(target().uri.toString())
        )
    }

    override fun configure(): Application {
        return OpenApiResourceConfig(
            OpenApiServerConfig(
                projectName = "Test",
                host = "localhost",
                port = 9090,
                nodeEndpoint = URL("http://localhost:8545"),
                privateKey = PRIVATE_KEY
            )
        )
    }

    @Test
    fun `find all deployed contracts`() {
        assertThat(client.contracts.findAll()).containsExactly("humanstandardtoken")
    }

    @Test
    fun `deploy and invoke contract`() {
        val receipt = client.contracts.humanStandardToken.deploy(
            HumanStandardTokenDeployParameters(
                BigInteger.TEN, "Test", BigInteger.ZERO, "TEST"
            )
        )

        client.contracts.humanStandardToken.load(receipt.contractAddress).apply {
            assertThat(approve(ApproveParameters(ADDRESS, BigInteger.TEN))).isNotNull()
            assertThat(decimals().result).isEqualTo(BigInteger.ZERO)
            assertThat(symbol().result).isEqualTo("TEST")
        }
    }
    
    companion object {
        private const val ADDRESS = "fe3b557e8fb62b89f4916b721be55ceb828dbd73"
        private const val PRIVATE_KEY = "8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
    }
}
