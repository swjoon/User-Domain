import SignUpForm from '@/components/SignUpForm'
import Header from '@/components/Header'

export default function SignUpPage() {
  return (
    <div className="min-h-screen bg-neutral-100">
      <Header />
      <div className="flex items-center justify-center h-[calc(100vh-64px)]">
        <SignUpForm />
      </div>
    </div>
  )
}