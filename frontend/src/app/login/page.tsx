import LoginForm from '@/components/LoginForm'
import Header from '@/components/Header'

export default function LoginPage() {
  return (
    <div className="min-h-screen bg-neutral-100">
      <Header />
      <div className="flex items-center justify-center h-[calc(100vh-64px)]">
        <LoginForm />
      </div>
    </div>
  )
}