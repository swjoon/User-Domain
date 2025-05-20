"use client";
import { useEffect } from "react";

export default function OAuth2Callback() {
  useEffect(() => {
    const url = new URL(window.location.href);
    const uuid = url.searchParams.get("UUID");
    const provider = url.searchParams.get("PROVIDER");
    const accessToken = url.searchParams.get("AccessToken");

    if (accessToken && uuid) {
      console.log("로그인 성공");

      window.opener?.postMessage(
        { type: "OAUTH2_LOGIN_SUCCESS", uuid, accessToken },
        "*"
      );
    } else if (uuid && provider) {
      console.log("소셜 회원가입");

      window.opener?.postMessage(
        { type: "OAUTH2_SIGNUP_REQUIRED", uuid, provider },
        "*"
      );
    } else {
      console.error("오류 발생");
    }

    window.history.replaceState({}, "", "/oauth2/callback");

    setTimeout(() => window.close(), 100);
  }, []);

  return <p>처리 중입니다...</p>;
}
