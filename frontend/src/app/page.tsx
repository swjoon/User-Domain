'use client';

import { useEffect, useState } from 'react';
import Header from '@/components/Header'
import { getUser } from '@/lib/auth'

export default function Home() {
  const [user, setUser] = useState<any>(null);

  useEffect(() => {
    const storedUser = getUser();
    setUser(storedUser);
  }, []);

  return (
    <div className="min-h-screen bg-white">
      <Header />
      <main className="px-8 py-12 max-w-4xl mx-auto">
        {user ? (
          <>
            <h1 className="text-3xl font-bold text-neutral-800">환영합니다, {user.name}님 👋</h1>
            <p className="mt-4 text-gray-600">오늘도 멋진 하루를 시작해볼까요?</p>
          </>
        ) : (
          <>
            <h1 className="text-3xl font-bold text-neutral-800">지금 시작해보세요</h1>
            <p className="mt-4 text-gray-600">계정을 생성하고 새로운 경험을 시작하세요.</p>
          </>
        )}
      </main>
    </div>
  )
}  