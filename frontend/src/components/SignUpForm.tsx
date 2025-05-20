'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import api from '@/lib/api'

export default function SignUpForm() {
  const router = useRouter()
  const [form, setForm] = useState({
    username: '',
    password: '',
    email: '',
    name: '',
    phone: '',
    gender: 'MALE',
    birthDate: '',
    zipCode: '',
    address: '',
    detailAddress: '',
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setForm(prev => ({
      ...prev,
      [e.target.name]: e.target.value,
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const response = await api.post('/api/v1/users/signup', form)

      const accessToken = response.headers['Authorization']
      const user = response.data?.data

      if (accessToken) localStorage.setItem('accessToken', accessToken)
        
      if (user) localStorage.setItem('user', JSON.stringify(user))

      router.push('/')
    } catch (error) {
      console.error('회원가입 실패:', error)
    }
  }

  return (
    <div className="bg-gray-50 flex justify-center items-start">
      <div className="w-full max-w-lg bg-white p-8 rounded-2xl shadow-md">
        <h2 className="text-2xl font-bold text-neutral-800 mb-1">회원가입</h2>
        <p className="text-sm text-gray-500 mb-6">정보를 입력해 계정을 생성해보세요.</p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            name="username"
            placeholder="아이디"
            value={form.username}
            onChange={handleChange}
            required
            className="w-full border p-3 rounded-lg"
          />
          <input
            name="password"
            type="password"
            placeholder="비밀번호"
            value={form.password}
            onChange={handleChange}
            required
            className="w-full border p-3 rounded-lg"
          />
          <input
            name="email"
            type="email"
            placeholder="이메일"
            value={form.email}
            onChange={handleChange}
            required
            className="w-full border p-3 rounded-lg"
          />
          <input
            name="name"
            placeholder="이름"
            value={form.name}
            onChange={handleChange}
            required
            className="w-full border p-3 rounded-lg"
          />
          <input
            name="phone"
            placeholder="휴대폰 번호"
            value={form.phone}
            onChange={handleChange}
            required
            className="w-full border p-3 rounded-lg"
          />
          <select
            name="gender"
            value={form.gender}
            onChange={handleChange}
            required
            className="w-full border p-3 rounded-lg"
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
            required
            className="w-full border p-3 rounded-lg"
          />
          <input
            name="zipCode"
            placeholder="우편번호"
            value={form.zipCode}
            onChange={handleChange}
            required
            className="w-full border p-3 rounded-lg"
          />
          <input
            name="address"
            placeholder="주소"
            value={form.address}
            onChange={handleChange}
            required
            className="w-full border p-3 rounded-lg"
          />
          <input
            name="detailAddress"
            placeholder="상세 주소"
            value={form.detailAddress}
            onChange={handleChange}
            required
            className="w-full border p-3 rounded-lg"
          />
          <button
            type="submit"
            className="w-full bg-neutral-800 text-white p-3 rounded-lg hover:bg-neutral-700"
          >
            회원가입 완료
          </button>
        </form>
      </div>
    </div>
  )
}
