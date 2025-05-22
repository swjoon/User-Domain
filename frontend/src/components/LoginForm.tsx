"use client";
import { useRouter } from "next/navigation";
import { useState, useEffect } from "react";
import api from "@/lib/api";
import Link from "next/link";

const ALLOWED_ORIGINS = ["http://localhost:3000", "http://localhost:8080"];

export default function LoginForm() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const router = useRouter();

  useEffect(() => {
    const handler = async (event: MessageEvent) => {
      if (!ALLOWED_ORIGINS.includes(event.origin)) return;

      const { type, uuid, provider, accessToken } = event.data;

      if (type === "OAUTH2_SIGNUP_REQUIRED") {
        router.push(`/signup/oauth2?uuid=${uuid}&provider=${provider}`);
      } else if (type === "OAUTH2_LOGIN_SUCCESS") {
        const bearerToken = "Bearer " + accessToken;
        localStorage.setItem("accessToken", bearerToken);

        try {
          const response = await api.get("/api/v1/users/token/info", {
            headers: {
              Authorization: bearerToken,
              "X-UUID": uuid,
            },
            withCredentials: true,
          });

          const user = response.data?.data;

          if (user) {
            localStorage.setItem("user", JSON.stringify(user));
          }
        } catch (err) {
          console.error("refreshToken 발급 실패:", err);
        }

        router.push("/");
      }
    };

    window.addEventListener("message", handler);
    return () => window.removeEventListener("message", handler);
  }, []);

  const openOAuthPopup = (provider: string) => {
    const popup = window.open(
      `http://localhost:8080/oauth2/authorization/${provider}`,
      "oauth2-login",
      "width=500,height=600"
    );

    const timer = setInterval(() => {
      if (popup?.closed) {
        clearInterval(timer);
      }
    }, 500);
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.post(
        "/api/v1/users/login",
        { username, password },
        {
          withCredentials: true,
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response) {
        throw new Error("로그인 실패");
      }

      const accessToken = response.headers["Authorization"];
      const user = response.data?.data;

      if (accessToken) localStorage.setItem("accessToken", accessToken);

      if (user) localStorage.setItem("user", JSON.stringify(user));
      router.push("/");
    } catch (error) {
      alert("로그인에 실패했습니다.");
    }
  };

  return (
    <div className="max-w-md w-full bg-white rounded-xl shadow-md p-8 space-y-4">
      <h2 className="text-xl font-semibold text-neutral-800 text-center">
        로그인
      </h2>
      <form onSubmit={handleLogin} className="space-y-4">
        <input
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="아이디"
          className="w-full border border-gray-300 rounded p-3 focus:outline-none focus:ring-2 focus:ring-neutral-500"
        />
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="비밀번호"
          className="w-full border border-gray-300 rounded p-3 focus:outline-none focus:ring-2 focus:ring-neutral-500"
        />
        <button
          type="submit"
          className="w-full bg-neutral-900 text-white py-3 rounded hover:bg-neutral-700 transition-colors"
        >
          로그인
        </button>
      </form>
      <div className="flex flex-col gap-2 mt-4">
        <button
          className="flex items-center justify-center gap-2 border border-gray-300 rounded py-2 hover:bg-gray-100"
          onClick={() => openOAuthPopup("google")}
        >
          <img src="/google-icon.svg" alt="Google" className="h-5 w-5" />
          Google 로그인
        </button>
        <button
          className="flex items-center justify-center gap-2 border border-gray-300 rounded py-2 hover:bg-gray-100"
          onClick={() => openOAuthPopup("kakao")}
        >
          <img src="/kakao-icon.svg" alt="Kakao" className="h-5 w-5" />
          Kakao 로그인
        </button>
        <button
          className="flex items-center justify-center gap-2 border border-gray-300 rounded py-2 hover:bg-gray-100"
          onClick={() => openOAuthPopup("naver")}
        >
          <img src="/naver-icon.svg" alt="Naver" className="h-5 w-5" />
          Naver 로그인
        </button>
      </div>

      <p className="text-sm text-center text-gray-500">
        계정이 없으신가요?{" "}
        <Link href="/signup" className="underline">
          회원가입
        </Link>
      </p>
    </div>
  );
}
