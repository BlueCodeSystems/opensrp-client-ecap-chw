package ecap.smartregister.chw.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import ecap.smartregister.chw.application.ChwApplication;
import ecap.smartregister.chw.contract.PinViewContract;
import ecap.smartregister.chw.fragment.ChooseLoginMethodFragment;
import ecap.smartregister.chw.fragment.PinLoginFragment;
import ecap.smartregister.chw.fragment.SetPinFragment;
import ecap.smartregister.chw.pinlogin.PinLogger;
import ecap.smartregister.chw.pinlogin.PinLoginUtil;

import ecap.smartregister.chw.R;

public class PinLoginActivity extends AppCompatActivity implements PinViewContract.Controller {
    public static final String DESTINATION_FRAGMENT = "DESTINATION_FRAGMENT";

    private PinLogger pinLogger = PinLoginUtil.getPinLogger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_login);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String destination = bundle.getString(DESTINATION_FRAGMENT);
            if (destination != null)
                navigateToFragment(destination);
        } else {
            switchToFragment(new PinLoginFragment());
        }

    }

    private void switchToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }

    @Override
    public void navigateToFragment(String destinationFragment) {
        switch (destinationFragment) {
            case ChooseLoginMethodFragment.TAG:
                switchToFragment(new ChooseLoginMethodFragment());
                break;
            case SetPinFragment.TAG:
                switchToFragment(new SetPinFragment());
                break;
            case PinLoginFragment.TAG:
                switchToFragment(new PinLoginFragment());
                break;
            default:
                break;
        }
    }

    @Override
    public void startPasswordLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void startHomeActivity() {
        Intent intent = new Intent(this, ChwApplication.getApplicationFlavor().launchChildClientsAtLogin() ?
                ChildRegisterActivity.class : FamilyRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public PinLogger getPinLogger() {
        return pinLogger;
    }
}
