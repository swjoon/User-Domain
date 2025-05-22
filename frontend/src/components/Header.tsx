"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
import { getUser } from "@/lib/auth";
import api from "@/lib/api";

export default function Header() {
  const [user, setUser] = useState<any>(null);

  useEffect(() => {
    setUser(getUser());
  }, []);

  const logout = async () => {
    const accessToken = localStorage.getItem("accessToken");

    try {
      await api.post("/api/v1/logout",
        {},
        {
          headers: {
            Authorization: accessToken ? `Bearer ${accessToken}` : "",
          },
          withCredentials: true,
        }
      );
    } catch (e) {
      console.error("서버 로그아웃 실패:", e);
    } finally {
      localStorage.removeItem("user");
      localStorage.removeItem("accessToken");
      location.reload();
    }
  };

  return (
    <header className="flex justify-between items-center px-6 py-4 bg-white shadow">
      <div className="text-xl font-bold text-neutral-800">
        <Link href="/">🧭 BaseHub</Link>
      </div>
      <div>
        {user ? (
          <div className="flex items-center gap-4">
            <span className="text-sm text-gray-700">{user.name}님</span>
            <button
              onClick={logout}
              className="text-sm text-neutral-700 hover:underline"
            >
              로그아웃
            </button>
          </div>
        ) : (
          <Link
            href="/login"
            className="text-sm text-neutral-700 hover:underline"
          >
            로그인
          </Link>
        )}
      </div>
    </header>
  );
}
