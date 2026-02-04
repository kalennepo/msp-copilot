package com.example.mspcopilot.application;

import org.springframework.stereotype.Service;

import com.example.mspcopilot.domain.enums.Category;

@Service
public class TriageService {

  public record TriageResult(Category category, String runbookSuggestion) {}

  public TriageResult triage(String title, String description) {
    String text = (title + " " + description).toLowerCase();

    if (containsAny(text, "password", "locked out", "mfa", "multi factor", "2fa", "login")) {
      return new TriageResult(Category.AUTH, "Runbook: Verify identity → check lockout/MFA status → reset password if needed → confirm login.");
    }
    if (containsAny(text, "vpn", "anyconnect", "tunnel", "remote access")) {
      return new TriageResult(Category.VPN, "Runbook: Check VPN outage → verify client version → reset network → re-test connection.");
    }
    if (containsAny(text, "outlook", "o365", "office 365", "exchange", "mailbox", "email")) {
      return new TriageResult(Category.EMAIL, "Runbook: Check service health → verify license → re-auth → rebuild profile if needed.");
    }
    if (containsAny(text, "wifi", "wireless", "dns", "network", "cannot reach", "latency")) {
      return new TriageResult(Category.NETWORK, "Runbook: Verify outage scope → DNS check → gateway ping → isolate to device vs network.");
    }
    if (containsAny(text, "laptop", "desktop", "drivers", "blue screen", "bsod", "slow")) {
      return new TriageResult(Category.DEVICE, "Runbook: Collect device info → check recent changes → run health checks → remediate drivers/software.");
    }

    return new TriageResult(Category.OTHER, "Runbook: Gather missing details → reproduce issue → route to appropriate team.");
  }

  private boolean containsAny(String text, String... needles) {
    for (String n : needles) if (text.contains(n)) return true;
    return false;
  }
}
