import React from 'react'
import { Globe } from 'lucide-react'
import { useTranslation } from '../contexts/TranslationContext'

export default function LanguageToggle() {
  const { language, toggleLanguage } = useTranslation()

  return (
    <button
      onClick={toggleLanguage}
      className="flex items-center gap-2 px-3 py-1.5 bg-white border border-slate-200 rounded-lg hover:bg-slate-50 transition-all text-sm font-medium text-slate-700 group"
      title={language === 'en' ? 'Switch to Portuguese' : 'Mudar para Inglês'}
    >
      <Globe className="w-4 h-4 text-slate-400 group-hover:text-slate-600 transition-colors" />
      <span className="font-mono text-xs uppercase tracking-wider">
        {language === 'en' ? 'EN' : 'PT'}
      </span>
    </button>
  )
}
