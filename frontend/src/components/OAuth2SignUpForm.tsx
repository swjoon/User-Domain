'use client';

import { useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import api from "@/lib/api";
import type { OAuth2SignUp } from "@/types/signup";

export default function OAuth2SignUpForm() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const [uuid, setUuid] = useState("");
  const [provider, setProvider] = useState("");
  const [form, setForm] = useState<OAuth2SignUp>({
    nickName: "",
    gender: "MALE",
    birthDate: "",
    zipCode: "",
    address: "",
    detailAddress: "",
    uuid: "",
    phoneNumber: "",
  });

  useEffect(() => {
    const queryUuid = searchParams.get("uuid");
    const queryProvider = searchParams.get("provider");

    if (!queryUuid || !queryProvider) return;

    setUuid(queryUuid);
    setProvider(queryProvider);
    setForm((prev) => ({ ...prev, uuid: queryUuid }));
  }, [searchParams]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    setForm((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = { ...form, uuid };
      if (provider !== "GOOGLE") {
        delete payload.phoneNumber;
      }

      const response = await api.post("/api/v1/users/signup/oauth2", payload);

      const accessToken = response.headers["Authorization"];
      const user = response.data?.data;

      if (accessToken) {
        localStorage.setItem("accessToken", accessToken);
      }

      if (user) {
        localStorage.setItem("user", JSON.stringify(user));
      }

      router.push("/");
    } catch (error) {
      console.error("OAuth2 회원가입 실패:", error);
    }
  };

  return (
    <div className="flex items-center justify-center bg-gray-50">
      <div className="w-full max-w-lg bg-white p-8 rounded-2xl shadow-md">
        <h2 className="text-2xl font-bold text-neutral-800 mb-1">회원가입</h2>
        <p className="text-sm text-gray-500 mb-6">
          소셜 로그인을 통해 계정을 만들고 서비스를 이용해보세요.
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          {provider === "GOOGLE" && (
            <input
              name="phoneNumber"
              value={form.phoneNumber || ""}
              onChange={handleChange}
              placeholder="전화번호"
              className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-neutral-600"
              required
            />
          )}
          <input
            name="nickName"
            value={form.nickName}
            onChange={handleChange}
            placeholder="닉네임"
            className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-neutral-600"
            required
          />
          <select
            name="gender"
            value={form.gender}
            onChange={handleChange}
            className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-neutral-600"
            required
          >
            <option value="">성별 선택</option>
            <option value="MALE">남성</option>
            <option value="FEMALE">여성</option>
          </select>
          <input
            name="birthDate"
            type="date"
            value={form.birthDate}
            onChange={handleChange}
            className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-neutral-600"
            required
          />
          <input
            name="zipCode"
            value={form.zipCode}
            onChange={handleChange}
            placeholder="우편번호"
            className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-neutral-600"
            required
          />
          <input
            name="address"
            value={form.address}
            onChange={handleChange}
            placeholder="주소"
            className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-neutral-600"
            required
          />
          <input
            name="detailAddress"
            value={form.detailAddress}
            onChange={handleChange}
            placeholder="상세 주소"
            className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-neutral-600"
            required
          />
          <button
            type="submit"
            className="w-full bg-neutral-800 text-white p-3 rounded-lg hover:bg-neutral-700 transition-colors"
          >
            회원가입 완료
          </button>
        </form>
      </div>
    </div>
  );
}
