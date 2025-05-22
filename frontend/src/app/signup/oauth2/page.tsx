import OAuth2SignUpForm from '@/components/OAuth2SignUpForm'
import Header from '@/components/Header'

export default function OAuth2SignUpPage(){
    return (
        <div className="min-h-screen bg-neutral-100">
          <Header />
          <div className="flex items-center justify-center h-[calc(100vh-64px)]">
            <OAuth2SignUpForm />
          </div>
        </div>
      )
}