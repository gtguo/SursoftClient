package cn.sursoft.CommandFactory;

/**
 * Created by gtguo on 5/17/2017.
 */

public class FactoryTestClient{
    private ParameterAssemble parameterAssemble;
    public FactoryTestClient(ParameterAssemble parameter){
        this.parameterAssemble = parameter;
    }

    public ParameterAssemble getParameterAssemble(){
        return this.parameterAssemble;
    }

    public TestScriptHandler createTestClient(TestTypeEnum testTpyeEnum){
        TestScriptHandler testScriptHandler = null;
        switch (testTpyeEnum){
            case CTS:
                testScriptHandler = new CTSTestCmd(getParameterAssemble());
                break;
            case LOWMEMORY:
                testScriptHandler = new LowMemoryTestCmd(getParameterAssemble());
                break;
            case MONKEY:
                testScriptHandler = new MonkeyTestCmd(getParameterAssemble());
                break;
            case STABILITY:
                testScriptHandler = new StabilityTestCmd(getParameterAssemble());
                break;
            default:
                System.out.println("The test Type is unsupported!");
                break;
        }
        return testScriptHandler;
    }
    /********************
    public static void main(String[] args){

        FactoryTestClient factoryTestClient = new FactoryTestClient();
        factoryTestClient.createTestClient(TestTypeEnum.CTS).execTestCmd();
        factoryTestClient.createTestClient(TestTypeEnum.LOWMEMORY).execTestCmd();
        factoryTestClient.createTestClient(TestTypeEnum.MONKEY).execTestCmd();
        factoryTestClient.createTestClient(TestTypeEnum.STABILITY).execTestCmd();

    }
     ******************/
}
