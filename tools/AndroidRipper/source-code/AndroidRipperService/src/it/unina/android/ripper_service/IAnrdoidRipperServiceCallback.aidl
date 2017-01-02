package it.unina.android.ripper_service;

import java.util.Map;

oneway interface IAnrdoidRipperServiceCallback {
    void receive(in Map message);
}